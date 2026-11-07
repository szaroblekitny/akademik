# Akademik – plan dokończenia projektu

Utworzony 2026-11-07. Ten plik jest główną wersją planu; postęp odhaczamy tutaj, w dzienniku na końcu.

## Jak korzystać z tego planu

Plan ma cztery etapy, od najprostszego do najbardziej ambitnego. Realizujemy je po jednym kroku: krok to jedna mała, sprawdzalna zmiana zakończona osobnym commitem.

Zasady pracy:

1. **Najpierw objaśnienie, potem kod.** Przed każdym krokiem opisuję, co zmieniam i dlaczego. Szczególnie zaznaczam rzeczy, które w Javie i Springu pojawiły się po Javie 8.
2. **Testy uruchamiasz Ty.** Po każdej zmianie podaję komendę (zwykle `mvn test`) i piszę, czego się spodziewać: ile testów, jaki komunikat, co oznaczałby błąd. Sam uruchamiasz, sprawdzasz i mówisz mi, co wyszło. Krok jest skończony dopiero, gdy potwierdzisz, że testy są zielone.
3. **Jeden krok = jeden commit na osobnej gałęzi.** Dzięki temu w historii Gita widać dokładnie, co się zmieniło, i łatwo cofnąć pomyłkę.
4. **Ty decydujesz o tempie.** Po każdym kroku zatrzymuję się, a Ty możesz dopytać, zmienić kierunek albo napisać coś samodzielnie.
5. **Dziennik na końcu dokumentu** pokazuje, co już zrobione.

Kroki oznaczone numerami (np. 1.2) służą do odwołań w rozmowie: wystarczy napisać „robimy 1.2”.

## Stan obecny projektu

Projekt (wersja 0.60-SNAPSHOT) kompiluje się, ale 9 z 36 testów pada, a import i eksport są tylko zarysowane. Ostatnia nowa funkcja (opróżnianie akademika) pochodzi z grudnia 2021. Późniejsze commity to głównie podbicia wersji i przejście z `javax` na `jakarta`.

**Technologie:**

- Java 21 (kompilacja), Spring 6.2 (bez Spring Boota),
- Spring Data JPA (Jakarta Persistence API, standard mapowania obiektów na tabele bazy) z Hibernate 6.6 jako implementacją,
- JSF 4.1 (Jakarta Faces, dawniej JavaServer Faces, czyli strony WWW budowane z komponentów) w implementacji Mojarra + biblioteka komponentów PrimeFaces 15,
- XStream do zapisu i odczytu XML,
- Atomikos jako menedżer JTA (Jakarta Transactions API, transakcje obejmujące kilka zasobów naraz),
- PostgreSQL pobierany przez JNDI (Java Naming and Directory Interface, katalog zasobów serwera, z którego aplikacja bierze połączenie do bazy skonfigurowane w Tomcacie),
- HSQLDB (baza danych w pamięci) w testach.

**Warstwy aplikacji:**

| Pakiet | Rola | Najważniejsze klasy |
| --- | --- | --- |
| `conf` | konfiguracja Springa w Javie (bez XML) | `MainConfiguration`, `AkademikConfiguration`, `AkademikXStream` |
| `entity` | encje JPA, czyli tabele bazy | `Pokoj`, `Student`, `Plec` + `KonwerterPlci` |
| `repo` | dostęp do bazy przez Spring Data | `PokojRepository`, `StudentRepository` |
| `core` | logika biznesowa | `Akademik` (kwaterowanie), `Plikowanie` (XML) |
| `namedbean` | beany dla ekranów JSF | `ZakwaterujBean`, `StudentBean`, `PokojBean`, `ImportBean`, `EksportBean` |
| `webapp` | strony XHTML | `akademik.xhtml`, `student.xhtml`, `pokoj.xhtml`, `kwateruj.xhtml`, `import.xhtml`, `eksport.xhtml` |

**Znane problemy:**

| # | Problem | Gdzie | Etap |
| --- | --- | --- | --- |
| P1 | 9 testów pada: Mockito 5.13 nie obsługuje Javy 25, którą masz zainstalowaną | `pom.xml` | 1 |
| P2 | Stare wtyczki Maven (surefire 2.22.2, war 3.2.3, clean 3.1.0); 31 z 36 testów nadal w JUnit 4 (przez `junit-vintage-engine`) | `pom.xml`, `src/test` | 1 |
| P3 | `compareTo` sortuje malejąco i może przepełnić `int` | `Pokoj`, `Student` | 1 |
| P4 | Przestarzały dialekt `PostgreSQL10Dialect` | `MainConfiguration` | 1 |
| P5 | Import zapisuje plik do `h:/TEMP/` i nie ładuje go do bazy | `ImportBean` | 2 |
| P6 | Eksport pobiera obrazek żyrafy zamiast danych | `EksportBean` | 2 |
| P7 | Brak raportu XML/HTML zapowiedzianego w `opis.html` | `Akademik` | 2 |
| P8 | ID nadawane przez `findLastId() + 1`, co daje wyścig przy dwóch użytkownikach | `StudentBean`, `PokojBean` | 3 |
| P9 | Algorytm marnuje miejsca: jedna kobieta blokuje duży pokój | `Akademik.kwaterowanie` | 3 |
| P10 | Stare przestrzenie nazw `java.sun.com/jsf` w XHTML i nagłówek `web.xml` 3.1 | `webapp` | 1 |

## Etap 1: zielone testy i porządki

Cel: `mvn test` przechodzi w całości na Javie 25, a drobne błędy znikają. Funkcje aplikacji się nie zmieniają. To fundament pod resztę.

**1.1 Podbicie Mockito (P1).** Z 5.13.0 na 5.24.0.

- *Dlaczego pada:* Mockito tworzy atrapy klas, generując w locie bajtkod przez bibliotekę ByteBuddy. Każda nowa Java zmienia format plików `.class` (numer wersji), a stary ByteBuddy nie zna formatu Javy 25. Stąd komunikat „Mockito cannot mock this class”.
- *Pułapka znaleziona przy realizacji:* ByteBuddy przychodził do projektu dwiema drogami: z Mockito i z Hibernate'a (1.14.18). Przy dwóch wersjach na tej samej głębokości drzewa Maven wybiera tę z zależności zadeklarowanej wcześniej w `pom.xml`, czyli z Hibernate'a. Dlatego samo podbicie Mockito by nie pomogło. Rozwiązanie: sekcja `<dependencyManagement>` ustala wersję ByteBuddy na 1.17.7.
- *Druga pułapka:* po naprawie Mockito JaCoCo 0.8.11 zgłaszał `IllegalClassFormatException` przy klasach `...$MockitoMock$...`. Mocki są generowane w trakcie testów w formacie Javy 25 (bo na niej działa JVM), a JaCoCo 0.8.11 tego formatu nie zna. Wcześniej błąd się nie pojawiał, bo Mockito w ogóle nie tworzył mocków. Rozwiązanie: JaCoCo 0.8.16.
- *Nowość:* plik `mockito-extensions/...MockMaker` z `mock-maker-inline` jest od Mockito 5 zbędny, bo to już domyślne ustawienie. Usuniemy go.
- *Nowość w Javie 21+:* przy agencie ładowanym dynamicznie JVM wypisuje ostrzeżenie. Rozwiążemy to, dodając Mockito jako `-javaagent` w surefire.

**1.2 Wtyczki Maven i kompilator (P2).** Surefire 2.22.2 → 3.6.1, war 3.2.3 → 3.5.1, clean 3.1.0 → 3.5.0. Usunięcie zależności `junit-platform-commons`, która była bez `<scope>test</scope>` i trafiała do WAR-a (Web Application Archive, paczka z aplikacją wdrażana do Tomcata).

- Kompilator dostaje `<release>21</release>` zamiast trójki `source`/`target`/`compilerVersion`. Opcja `--release` (od Javy 9) pilnuje też, żeby w kodzie nie dało się użyć API nowszego niż Java 21.
- Wszystkie trzy wtyczki wymagają Mavena co najmniej 3.6.3 (używany jest Maven 3.10.0).
- *Korekta planu:* pierwotnie w tym kroku miało też zniknąć `junit-vintage-engine`. Okazało się, że w JUnit 5 jest tylko `PokojBeanTest` (5 testów), a 10 pozostałych klas (31 testów) to nadal JUnit 4. Bez Vintage te testy przestałyby się w ogóle uruchamiać. Przenosimy to do kroku 1.2a.

**1.2a Migracja testów na JUnit 5 (P2).** Przepisanie 10 klas testowych z JUnit 4 na JUnit 5 (Jupiter), a na końcu usunięcie `junit-vintage-engine`.

- *Objaśnienie:* JUnit 5 to nie jedna biblioteka, tylko platforma z „silnikami”. Jupiter uruchamia testy JUnit 5, Vintage stare testy JUnit 4. Po migracji Vintage nie będzie potrzebny.
- Główne zamiany: `org.junit.Test` → `org.junit.jupiter.api.Test`, `@Before`/`@After` → `@BeforeEach`/`@AfterEach`, `Assert.assertEquals` → `Assertions.assertEquals`, `@RunWith(SpringJUnit4ClassRunner.class)` + `@ContextConfiguration` → `@SpringJUnitConfig`. Uwaga na kolejność argumentów: w obu wersjach komunikat błędu przy asercjach stoi w innym miejscu (w JUnit 4 pierwszy, w JUnit 5 ostatni).
- Wzorzec jest już w `PokojBeanTest`, przerobionym w grudniu 2025.
- *Kolejność testów:* JUnit 4 i JUnit 5 wybierają kolejność metod testowych inaczej (oba deterministycznie, ale według innego algorytmu). Testy, które zostawiają dane w bazie HSQLDB dla następnych, mogą po migracji zacząć padać. To byłby sygnał, że testy nie są od siebie niezależne, a nie błąd migracji.
- Proponowana kolejność: najpierw proste testy bez Springa (`PokojTest`, `StudentTest`, `AkademikXStreamTest`), potem te z kontekstem Springa. Po każdej klasie liczba testów musi zostać ta sama (36).

**1.2b Ostrzeżenia widoczne przy testach.** Dwa ostrzeżenia zauważone po kroku 1.2.

- *Spring, `BeanPostProcessorChecker`:* metoda `exceptionTranslation()` w `AkademikConfiguration` tworzy `BeanPostProcessor`, ale nie jest `static`. Spring musi więc najpierw utworzyć całą klasę konfiguracyjną, zanim zarejestruje postprocesor, a wtedy ta klasa sama nie przechodzi przez wszystkie postprocesory. Poprawka: `public static PersistenceExceptionTranslationPostProcessor exceptionTranslation()`. Metoda statyczna może zostać wywołana bez tworzenia obiektu klasy.
- *JVM, `sun.misc.Unsafe::objectFieldOffset` wywołane przez XStream:* od Javy 24 (JEP 498; JEP to JDK Enhancement Proposal, numerowana propozycja zmiany w Javie) JVM ostrzega przy użyciu starych metod dostępu do pamięci z `sun.misc.Unsafe`, które mają zostać usunięte. XStream 1.4.21 to najnowsza wersja (listopad 2024), więc podbicie nie pomoże. Poprawka: w `AkademikXStream` tworzymy `new XStream(new PureJavaReflectionProvider())`. Ten wariant tworzy obiekty przez konstruktor bezargumentowy zamiast przez `Unsafe`, a `Pokoj` i `Student` taki konstruktor mają.
- *Wynik:* ostrzeżenie o `Unsafe` zostało. Bajtkod XStreama (`javap -c` na klasie `com.thoughtworks.xstream.core.JVM`) pokazuje, że jej statyczny inicjalizator (`static {}`) przy pierwszym użyciu XStreama sam tworzy `SunUnsafeReflectionProvider` i zapisuje nim pola testowej klasy `JVM$Test`, żeby sprawdzić, co działa na tej JVM. To dzieje się niezależnie od providera, którego używamy. `PureJavaReflectionProvider` zostaje, bo nasze własne wczytywanie XML-a nie korzysta już z `Unsafe`. Samo ostrzeżenie da się tylko wyciszyć opcją JVM `--sun-misc-unsafe-memory-access=allow` albo zaakceptować.
- *Decyzja:* ostrzeżenie zostaje. Jest prawdziwe i przypomina o zamianie XStreama w Etapie 4. Wyciszanie wymagałoby profilu Mavena tylko dla Javy 24+ (Java 21 nie zna tej opcji).
- *Uwaga na przyszłość:* XStream jest rozwijany bardzo powoli. Przy Etapie 4 warto rozważyć zamianę na Jackson XML albo Jakarta XML Binding (JAXB).
- Komunikat `HHH10005002 ... CDI is available` to tylko informacja (`INFO`), nie ostrzeżenie. Pojawia się, bo w testach jest na classpath API CDI (Contexts and Dependency Injection, standard Jakarty do wstrzykiwania zależności, konkurencyjny wobec Springa).

**1.3 Poprawka `compareTo` (P3).** Zamiast `(int)(inny.getId() - this.getId())` użyjemy `Long.compare(this.id, inny.id)`.

- *Dlaczego:* odejmowanie dwóch `long` i rzutowanie na `int` obcina wysokie bity, więc dla dużych ID wynik może mieć zły znak. Obecny kod sortuje też odwrotnie (malejąco). Najpierw napiszemy test, który to pokazuje, potem poprawkę. To jest TDD (Test-Driven Development, programowanie sterowane testami) w cyklu „czerwony, zielony, refaktor”: test najpierw pada, potem kod sprawia, że przechodzi, na końcu porządki bez zmiany działania.
- *Uwaga:* obecne testy `PokojTest.testPorownania` i `StudentTest.testPorownania` sprawdzają dokładne wartości z błędnej implementacji (`1` i `-3`). Kontrakt `compareTo` gwarantuje tylko znak wyniku, więc te testy trzeba będzie poprawić razem z kodem.
- *Wynik:* w każdej klasie trzy testy (kierunek, przepełnienie przy różnicy ID 2^32, sortowanie strumieniem). Faza czerwona dała dokładnie przewidziane 6 porażek na 40 testów, faza zielona 40/40. `compareTo` nie jest używane w kodzie aplikacji, więc zmiana nie wpływa na ekrany.
- *Przy okazji:* krzaczki w polskich komunikatach w Git Bash usunęła zmienna `MAVEN_OPTS="-Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"` w `~/.bashrc` (ustawienie terminala, nie projektu).

**1.4 Dialekt Hibernate (P4).** Usunięcie `hibernate.dialect`. Hibernate 6 sam rozpoznaje wersję PostgreSQL z połączenia, a klasy `PostgreSQL10Dialect` są oznaczone jako przestarzałe.

- *Dialekt* to klasa Hibernate'a, która wie, jak mówić SQL-em konkretnej bazy: jak wygląda stronicowanie, sekwencje, typy kolumn. Hibernate 6 odczytuje nazwę i wersję bazy z połączenia JDBC (Java Database Connectivity, standardowy interfejs do baz danych) i sam dobiera dialekt.
- *Korekta planu:* sprawdzenie zawartości `hibernate-core-6.6.4.Final.jar` pokazało, że klasy `org.hibernate.dialect.PostgreSQL10Dialect` w nim **nie ma** (jest tylko `PostgreSQLDialect`). Więc to nie było tylko przestarzałe ustawienie. Konfiguracja produkcyjna wskazywała klasę, której nie ma, więc aplikacja w Tomcacie najpewniej nie wystartowałaby od czasu przejścia na Hibernate 6 (grudzień 2024). Testy tego nie wykryły, bo `TestConfiguration` (HSQLDB) w ogóle nie ustawia dialektu.
- Testy nie sprawdzą tej zmiany, bo `MainConfiguration` działa tylko z PostgreSQL przez JNDI w Tomcacie. Weryfikacja w kroku 1.6.

**1.5 Przestrzenie nazw JSF (P10).** Zamiana `http://java.sun.com/jsf/...` na `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`. Aktualizacja `web.xml` do wersji 6.0 (Jakarta EE 10). Stare adresy działają jeszcze dzięki zgodności wstecz, ale Faces 4 oznacza je jako przestarzałe.

**1.6 Uruchomienie aplikacji w Tomcacie.** Sprawdzimy ręcznie, czy wszystkie ekrany działają po zmianach. Potrzebny będzie Tomcat 10.1 lub 11 i PostgreSQL. Przygotuję instrukcję i ewentualnie plik `docker-compose.yml` z bazą.

**Gotowe, gdy:** 36 testów zielonych, aplikacja startuje w Tomcacie, każdy ekran się otwiera.

## Etap 2: import, eksport i raport

Cel: aplikacja robi wszystko, co zapowiada `opis.html`. Większość klocków już istnieje (`Plikowanie`, `Akademik.pobierzZPliku`), trzeba je tylko połączyć z ekranami.

**2.1 Wspólna usługa `DaneService` (warstwa serwisowa).** Logikę „plik → baza” i „baza → plik” przeniesiemy z `Akademik` do osobnej klasy z adnotacją `@Service` i `@Transactional`.

- *Objaśnienie:* bean JSF powinien tylko obsługiwać ekran, a serwis wykonywać pracę w jednej transakcji. Jeśli import 50 studentów padnie na 30., `@Transactional` wycofa wszystko i baza nie zostanie w połowie wypełniona.
- *Nowość:* wstrzykiwanie przez konstruktor zamiast setterów z `@Autowired`. Pola mogą być wtedy `final`, a od Springa 4.3 przy jednym konstruktorze adnotacja `@Autowired` jest zbędna.

**2.2 Import do bazy (P5).** `ImportBean.upload()` przekaże strumień z przesłanego pliku prosto do `DaneService`, bez zapisu na dysk `h:/TEMP/`. Ekran pokaże, ile pokoi lub studentów wczytano.

- *Nowość:* `try-with-resources` (Java 7), czyli `try (InputStream in = ...) { }`. Strumień zamyka się sam, także przy wyjątku. Obecny kod zamyka strumienie ręcznie i przy błędzie zostawia je otwarte.
- *Decyzja do podjęcia:* czy import ma najpierw kasować istniejące dane (jak wersja plikowa), czy dopisywać?

**2.3 Eksport do XML (P6).** `EksportBean` zamiast żyrafy wygeneruje `pokoje.xml` i `studenci.xml` przez `Plikowanie.saveObjectList()`. Format będzie taki sam jak przy imporcie, więc eksport i import zrobią pełne koło.

- *Objaśnienie:* obecny `EksportBean` liczy plik w konstruktorze, raz na sesję. Przeniesiemy to do metody wywoływanej przy kliknięciu, żeby eksport zawsze pokazywał aktualne dane.

**2.4 Raport XML kwaterunku (P7).** Nowa metoda tworzy dokument XML: lista pokoi, w każdym zakwaterowani studenci, na końcu meldunek o wyniku.

- *Nowość (Java 16):* `record`, czyli niezmienna klasa danych w jednej linii: `record WierszRaportu(String pokoj, List<String> mieszkancy) {}`. Kompilator sam tworzy konstruktor, gettery, `equals`, `hashCode` i `toString`.

**2.5 Raport HTML przez XSLT (P7).** XSLT (Extensible Stylesheet Language Transformations) to język opisujący, jak przekształcić jeden dokument XML w inny, np. w HTML. Arkusz `raport.xsl` w zasobach przekształci XML w tabelę HTML. Użyjemy standardowego `javax.xml.transform` z JDK, bez dodatkowych bibliotek. Na ekranie kwaterowania pojawi się przycisk „Pobierz raport”.

**2.6 Testy.** Każdy krok dostaje test: import z przykładowego XML w `src/test/resources`, eksport i ponowny import dają te same dane, raport zawiera wszystkie pokoje.

**Gotowe, gdy:** da się wczytać pliki XML z ekranu, zakwaterować, pobrać raport HTML i wyeksportować dane.

## Etap 3: lepsze kwaterowanie i identyfikatory

Cel: algorytm mieści więcej studentów, a ID nadaje baza danych. Ten etap robimy ściśle metodą TDD: najpierw test, który pada, potem kod.

**3.1 Test pokazujący słabość algorytmu (P9).** Przykład: pokoje 4-osobowy i 1-osobowy, jedna kobieta i czterech mężczyzn. Obecny algorytm daje kobietę do pokoju 4-osobowego i trzech mężczyzn zostaje bez miejsca. Test będzie wymagał, żeby wszyscy się zmieścili.

**3.2 Nowy algorytm.** Propozycja prostej heurystyki: dla każdej płci policz liczbę osób, a potem przydzielaj pokoje tak, żeby suma miejsc była jak najbliższa potrzebom (najpierw grupa liczniejsza, największe pokoje najpierw). Jeśli się nie zmieszczą, komunikat poda, ilu osób zabrakło.

- *Nowość (Java 8+):* strumienie (`stream()`, `filter`, `sorted`, `collect`) i `Comparator.comparingInt(Pokoj::getLiczbaMiejsc).reversed()` zamiast ręcznych pętli z iteratorami.
- *Nowość (Java 16/21):* zamiast zwracać tekst „Kwaterowanie udane” i porównywać go w `ZakwaterujBean`, zwrócimy `record WynikKwaterowania(boolean udane, int niezakwaterowani, String komunikat)`. Porównywanie napisów jest kruche: literówka psuje logikę bez błędu kompilacji.

**3.3 ID z sekwencji bazy (P8).** `@Id @GeneratedValue(strategy = GenerationType.SEQUENCE)` w encjach i usunięcie `findLastId()`.

- *Dlaczego:* dwóch użytkowników naraz może odczytać ten sam ostatni ID i obaj zapiszą `ID + 1`. Sekwencja w bazie wydaje numery atomowo.
- *Uwaga:* pliki XML z importu zawierają własne ID. Trzeba będzie zdecydować, czy import je zachowuje, czy nadaje nowe.

**3.4 Walidacja danych.** Adnotacje Bean Validation (`@NotBlank`, `@Min(1)`) na polach encji, żeby nie dało się zapisać pokoju z 0 miejsc ani studenta bez nazwiska. JSF pokaże błędy automatycznie przy formularzu. Potrzebna będzie implementacja walidacji, czyli zależność Hibernate Validator.

**Gotowe, gdy:** test z 3.1 przechodzi, nowe rekordy dostają ID z sekwencji, formularze odrzucają błędne dane.

## Etap 4 (opcjonalny): Spring Boot i nowoczesne narzędzia

Cel: projekt wygląda jak typowa aplikacja Springa z 2026 roku. Ten etap daje najwięcej nauki, bo tak dziś pisze się aplikacje w pracy. Aktualne wersje (Maven Central, listopad 2026): Spring Framework 7.0.10, Spring Boot 4.1.2, Hibernate 7.4, JoinFaces 6.1.2.

**4.1 Najpierw Spring 7 bez Boota.** Podbicie Springa z 6.2 do 7.0 i Hibernate do 7.x jako osobny krok. Mniejsza zmiana, łatwiej znaleźć przyczynę, jeśli coś pęknie.

**4.2 Przejście na Spring Boot.** Boot zastępuje większość ręcznej konfiguracji „autokonfiguracją”: wykrywa biblioteki w classpath i sam tworzy dla nich beany.

| Dziś (ręcznie) | Po migracji (Boot) |
| --- | --- |
| `MainConfiguration`: `DataSource` z JNDI, `EntityManagerFactory`, Atomikos, `JtaTransactionManager` | kilka linii w `application.properties`; zwykłe transakcje JPA bez JTA |
| `web.xml` z listenerami Springa i `FacesServlet` | brak `web.xml`; JoinFaces rejestruje JSF |
| plik WAR wdrażany do zewnętrznego Tomcata | wykonywalny JAR (Java Archive) z wbudowanym Tomcatem: `java -jar akademik.jar` |
| wersje każdej biblioteki wpisane w `pom.xml` | jeden rodzic `spring-boot-starter-parent` dobiera zgodne wersje |
| `log4j2.xml` + kilka zależności logowania | domyślnie Logback, konfiguracja w `application.properties` |

- *Objaśnienie JTA:* Atomikos jest potrzebny tylko wtedy, gdy jedna transakcja obejmuje kilka zasobów naraz (np. dwie bazy albo bazę i kolejkę JMS, czyli Jakarta Messaging, standard kolejek komunikatów). Akademik ma jedną bazę, więc wystarczy zwykły `JpaTransactionManager`. Jeśli chcesz ćwiczyć JTA, zostawimy go świadomie.
- *Decyzja do podjęcia:* usuwamy Atomikos, czy zostawiamy go do ćwiczenia transakcji rozproszonych?
- *Do sprawdzenia przed startem:* zgodność JoinFaces 6.1 i PrimeFaces 15 z Bootem 4.1.

**4.3 Flyway zamiast luźnych skryptów SQL.** Skrypty z `src/main/sql` staną się migracjami `V1__schemat.sql`, `V2__dane.sql`. Flyway uruchomi je przy starcie aplikacji i zapamięta w tabeli, które już wykonał. Skrypty dla Oracle można usunąć albo zostawić jako osobny zestaw.

**4.4 Testcontainers zamiast HSQLDB.** Testy uruchomią prawdziwy PostgreSQL w kontenerze Dockera. HSQLDB zachowuje się inaczej niż PostgreSQL (typy, sekwencje), więc test może przejść na HSQLDB i paść w produkcji. Wymaga Docker Desktop.

**4.5 Opcjonalnie: alternatywa dla JSF.** JSF jest dziś niszowy. Jeśli chcesz poznać popularniejsze podejście, można dodać drugi interfejs: REST API (Representational State Transfer, usługi wymieniające dane, zwykle JSON, przez zwykłe żądania HTTP; w Springu `@RestController`) albo strony w Thymeleaf. Logika w `core` i serwisy z Etapu 2 zostaną bez zmian, i to dobrze pokazuje sens warstw.

**Gotowe, gdy:** `mvn spring-boot:run` uruchamia aplikację, a testy działają na PostgreSQL w kontenerze.

## Słowniczek nowości w Javie

To są nowości, które pojawią się w kodzie podczas prac. Każdą omówię szerzej przy pierwszym użyciu.

| Element | Od Javy | Przykład | Po co |
| --- | --- | --- | --- |
| try-with-resources | 7 | `try (var in = plik.getInputStream()) { ... }` | automatyczne zamykanie strumieni |
| lambdy i referencje metod | 8 | `studenci.forEach(s -> s.setPokoj(null))`, `Pokoj::getLiczbaMiejsc` | krótki kod zamiast klas anonimowych |
| strumienie (Stream API) | 8 | `pokoje.stream().filter(p -> p.getLiczbaMiejsc() > 1).toList()` | przetwarzanie kolekcji bez ręcznych pętli |
| `Optional` | 8 | `repo.findById(id).orElseThrow()` | jawne „może nie być wyniku” zamiast `null` |
| `var` | 10 | `var pokoje = pokojRepo.findAll();` | kompilator sam ustala typ zmiennej lokalnej |
| wyrażenia `switch` | 14 | `String s = switch (plec) { case KOBIETA -> "K"; case MEZCZYZNA -> "M"; };` | `switch` zwraca wartość, bez `break` |
| bloki tekstowe | 15 | `"""` wieloliniowy tekst `"""` | czytelny XML i SQL w testach |
| `record` | 16 | `record WynikKwaterowania(boolean udane, String komunikat) {}` | niezmienne klasy danych bez boilerplate'u |
| `instanceof` z wzorcem | 16 | `if (obj instanceof Pokoj p) { p.getId(); }` | bez dodatkowego rzutowania |
| `Stream.toList()` | 16 | `stream.toList()` | krócej niż `collect(Collectors.toList())` |
| wzorce typów w `switch` | 21 | `switch (obj) { case Pokoj p -> ...; case Student s -> ...; }` | rozpoznanie typu bez łańcucha `if`-ów (przyda się w `pobierzZPliku`) |
| kolekcje sekwencyjne | 21 | `lista.getFirst()`, `lista.getLast()` | zamiast `get(0)` i `get(size() - 1)` |

## Dziennik postępu

Po każdym kroku aktualizuję status i dopisuję skrót commita. Statusy: do zrobienia, w toku, zrobione, pominięte.

| Krok | Opis | Status | Commit |
| --- | --- | --- | --- |
| 1.1 | Podbicie Mockito (+ ByteBuddy 1.17.7, JaCoCo 0.8.16) | zrobione | 1c29a35 |
| 1.2 | Wtyczki Maven i kompilator `release` | zrobione | 0b3964e |
| 1.2a | Migracja 10 klas testów na JUnit 5, usunięcie JUnit Vintage | zrobione | cdb193b, 04225a9 |
| 1.2b | Ostrzeżenia: statyczny `BeanPostProcessor`, XStream bez `Unsafe` (ostrzeżenie z XStreama zostaje) | zrobione | e166b4a |
| 1.3 | Poprawka `compareTo` | zrobione (TDD: czerwona 6 porażek, zielona 40/40) | 819a9b2 |
| 1.4 | Dialekt Hibernate (klasa `PostgreSQL10Dialect` nie istniała) | zrobione, do sprawdzenia w Tomcacie w 1.6 | |
| 1.5 | Przestrzenie nazw JSF, `web.xml` 6.0 | do zrobienia | |
| 1.6 | Uruchomienie w Tomcacie | do zrobienia | |
| 2.1 | `DaneService` | do zrobienia | |
| 2.2 | Import do bazy | do zrobienia | |
| 2.3 | Eksport do XML | do zrobienia | |
| 2.4 | Raport XML | do zrobienia | |
| 2.5 | Raport HTML przez XSLT | do zrobienia | |
| 2.6 | Testy importu, eksportu i raportu | do zrobienia | |
| 3.1 | Test słabości algorytmu | do zrobienia | |
| 3.2 | Nowy algorytm kwaterowania | do zrobienia | |
| 3.3 | ID z sekwencji | do zrobienia | |
| 3.4 | Walidacja danych | do zrobienia | |
| 4.1 | Spring 7 i Hibernate 7 | do zrobienia | |
| 4.2 | Spring Boot 4 + JoinFaces | do zrobienia | |
| 4.3 | Flyway | do zrobienia | |
| 4.4 | Testcontainers | do zrobienia | |
| 4.5 | REST API lub Thymeleaf | do zrobienia | |
