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
6. **Issues z GitHuba.** Przy krokach, które rozwiązują issue, jest dopisek *Issues:*. Commit kończącego kroku ma w opisie `closes #N`, a commit, który rozwiązuje issue tylko częściowo, ma `refs #N`. GitHub zamknie issue sam, gdy commit trafi na gałąź `master`. Na gałęzi roboczej issue zostaje otwarte.

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

**Otwarte issues na GitHubie** (stan na 8.11.2026, [lista](https://github.com/szaroblekitny/akademik/issues)):

| Issue | Temat | Krok planu |
| --- | --- | --- |
| [#3](https://github.com/szaroblekitny/akademik/issues/3) | Niepotrzebne identyfikatory w plikach wejściowych | 2.3 (częściowo), 3.3 |
| [#7](https://github.com/szaroblekitny/akademik/issues/7) | Obsługa plików XML nie powinna używać XStream | 2.1a |
| [#8](https://github.com/szaroblekitny/akademik/issues/8) | Eksport pliku z danymi | 2.2 (częściowo), 2.4 |
| [#12](https://github.com/szaroblekitny/akademik/issues/12) | Ręczne wprowadzanie, edycja i kasowanie rekordów | 3.5 |
| [#14](https://github.com/szaroblekitny/akademik/issues/14) | Techniczna: moduły z Javy 9 | bez kroku, decyzja odłożona |
| [#16](https://github.com/szaroblekitny/akademik/issues/16) | Sortowanie wyniku działania | 2.7 |
| [#17](https://github.com/szaroblekitny/akademik/issues/17) | Plik logu ląduje w złym miejscu w Tomcacie | 1.7 |
| [#21](https://github.com/szaroblekitny/akademik/issues/21) | Generator danych | 3.6 |
| [#22](https://github.com/szaroblekitny/akademik/issues/22) | Przenieść konfigurację do pliku | 4.2 |
| [#23](https://github.com/szaroblekitny/akademik/issues/23) | Nazwy tabel | 4.3 |

[#14](https://github.com/szaroblekitny/akademik/issues/14) zostaje na razie bez działań. Uwaga do późniejszej decyzji: aplikacja WAR w Tomcacie nie korzysta z modułów JPMS (Java Platform Module System), bo Tomcat ładuje klasy po staremu, z classpath. Spring i Hibernate też modułów nie wymagają.

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

- *Wynik:* 12 plików XHTML, po 3 przestrzenie nazw w każdym (`h`, `f`, `ui`). Przestrzeń `p` (PrimeFaces, `http://primefaces.org/ui`) zostaje, bo to adres PrimeFaces, a nie Jakarty. `faces-config.xml` przeszedł z wersji 2.2 na 4.0, a `web.xml` z 3.1 na 6.0. Oba pliki zostały sprawdzone parserem XML.
- Zamiana w XHTML przez `sed -b`: tryb binarny zachowuje końce linii CRLF. Zwykłe `sed -i` w Git Bash zamieniłoby je na LF i diff pokazałby zmianę każdej linii.
- Testy nie obejmują stron JSF. Weryfikacja w Tomcacie w kroku 1.6.

**1.6 Uruchomienie aplikacji w Tomcacie.** Sprawdzimy ręcznie, czy wszystkie ekrany działają po zmianach. Potrzebny będzie Tomcat 10.1 lub 11 i PostgreSQL. Przygotuję instrukcję i ewentualnie plik `docker-compose.yml` z bazą.

- *Ustalenia (7.11.2026):* Dockera nie ma, PostgreSQL 18 jest zainstalowany lokalnie. Tomcat: najpierw próba na `H:\app\apache-tomcat-10.1.34`, ostatecznie Tomcat 11 (patrz korekta niżej). Do zrobienia w tym kroku: baza i użytkownik, tabele ze skryptów `src/main/sql`, zasób JNDI `jdbc/akademik` w konfiguracji Tomcata, wdrożenie WAR-a i ręczne sprawdzenie ekranów.
- *Baza:* skrypt `src/main/sql/setup_postgres.sql` (uruchamiany w PowerShellu przez `psql -U postgres -f ...`) zakłada rolę `tomcat`, bazę `akademik` i tabele z danymi przykładowymi, dołączając stare skrypty przez `\ir` (ścieżka liczona względem katalogu skryptu). Polskie znaki w `psql`: przed uruchomieniem `chcp 1250`, bo `psql` pisze w stronie kodowej Windows 1250, a konsola domyślnie czyta 852.
- *Korekta planu (Tomcat 11 i Weld):* pierwsze wdrożenie na Tomcacie 10.1 padło z `NoClassDefFoundError: jakarta/enterprise/inject/spi/el/ELAwareBeanManager`. Przyczyny są dwie. Po pierwsze, Faces 4.x wymaga CDI (Contexts and Dependency Injection), a Tomcat go nie ma. Po drugie, Mojarra 4.1 należy do Jakarta EE 11 (Servlet 6.1, EL 6.0, CDI 4.1), a Tomcat 10.1 do Jakarta EE 10. Wybrana droga: **Tomcat 11** zamiast cofania bibliotek, bo Etap 4 (Spring 7, Boot 4) i tak wymaga Jakarta EE 11. Zmiany:
  - w `pom.xml` doszedł `weld-servlet-core` 6.0.4.Final (CDI 4.1). Weld 7 to już CDI 5, czyli Jakarta EE 12, i nie pasuje;
  - usunięta deklaracja `jakarta.enterprise.cdi-el-api` z zakresem *test*. Bliższa deklaracja wygrywa, więc wycięłaby tę bibliotekę z WAR-a, a Weld przynosi ją w zakresie *compile*;
  - nowa klasa `conf/FacesCdiActivator` (`@FacesConfig @ApplicationScoped`). Weld startuje tylko wtedy, gdy znajdzie choć jeden bean CDI, a nasze beany są beanami Springa;
  - nowy `WEB-INF/beans.xml` w trybie `annotated`: Weld zajmuje się tylko klasami z adnotacjami CDI, a klasy Springa zostawia w spokoju.
- *Korekta planu (wersja JPA):* drugie wdrożenie padło w Springu: `EntityManagerFactory interface [org.hibernate.SessionFactory] seems to conflict...`, a głębiej `methods with same signature getSchemaManager() but incompatible return types`. Przyczyna: `pom.xml` wykluczał `jakarta.persistence-api` z Hibernate'a i dokładał wersję 3.2.0 (Jakarta EE 11). Tymczasem Hibernate 6.6 implementuje JPA (Jakarta Persistence) 3.1. W API 3.2 metoda `getSchemaManager()` zwraca inny typ niż w `SessionFactory` Hibernate'a, a Java nie umie zrobić proxy implementującego oba interfejsy. Poprawka: usunięte wykluczenie i deklaracja 3.2.0, więc API 3.1.0 przychodzi teraz z Hibernate'a. Testy tego nie wykrywały, bo `TestConfiguration` obchodzi problem przez `setEntityManagerFactoryInterface(jakarta.persistence.EntityManagerFactory.class)`. JPA 3.2 wróci razem z Hibernate 7 w Etapie 4.
- *Korekta planu (upload w PrimeFaces):* trzecie wdrożenie: Spring już wstał, ale padło `ClassNotFoundException: org.primefaces.webapp.filter.FileUploadFilter`. PrimeFaces od wersji 13 nie ma własnego filtra uploadu ani parametru `primefaces.UPLOADER` i czyta pliki standardowym API Servletu (`HttpServletRequest.getParts()`). Poprawka w `web.xml`: usunięte filtr, jego mapowanie i parametr, a przy `FacesServlet` doszło `<multipart-config>` (limit 10 MB na plik, 20 MB na żądanie).
- *Przy okazji (logowanie):* przy starcie pojawiało się ostrzeżenie `SLF4J(W): No SLF4J providers were found`. W WAR-ze jest `slf4j-api` 2.0, a most do Log4j był w starej wersji `log4j-slf4j-impl`, przeznaczonej dla SLF4J 1.7, więc SLF4J go ignorował. Logi bibliotek piszących przez SLF4J przepadały. Zamiana na `log4j-slf4j2-impl` (ta sama wersja Log4j).
- *Przy okazji (szum w logu):* w `log4j2.xml` logger `org.jboss.weld` na poziomie `info`. Główny logger (`Root`) ma `debug`, więc bez tego Weld wypisywał setki linii przy starcie.
- *Co sprawdzamy przy okazji:* krok 1.4 (Hibernate sam wykrywa PostgreSQL, brak błędu o dialekcie) i krok 1.5 (strony JSF z nowymi przestrzeniami nazw). **Potwierdzone 8.11.2026:** wszystkie sześć stron się renderuje, a listy pokazują 7 studentów i 4 pokoje z PostgreSQL.
- *Część 2, błędy na ekranach (8.11.2026):*
  - motyw `nova-light` w `web.xml` nie istnieje w PrimeFaces 15 (komunikat „Unable to find resource primefaces-nova-light, theme.css” i `<link href="RES_NOT_FOUND">` na każdej stronie). Biblioteka ma wbudowany tylko `saga-blue`, więc zmiana na `saga-blue`;
  - szablon dołączał `akademik.css` i `favicon.ico` adresem względnym. Pod `/akademik/faces/student.xhtml` przeglądarka szukała `/akademik/faces/akademik.css`, a ten adres pasuje do mapowania `/faces/*`. FacesServlet traktował więc plik CSS jak stronę, rzucał `ViewHandlingStrategyNotFoundException` i przekierowywał na `errors.xhtml`. Poprawka: `akademik.css` przeniesiony do `resources/css/` i dołączany przez `<h:outputStylesheet name="css/akademik.css"/>`, bo Faces sam buduje adres zasobu. Ikonka dostała adres `#{request.contextPath}/favicon.ico`;
  - zapis nowego pokoju: przycisk „Zapisz” kręcił kółkiem bez końca, choć pokój był już w bazie. `oncomplete="PF('pokoje').addRow();"` odwoływał się do widgetu `pokoje`, a tabela pokoi nie miała `widgetVar` (w odróżnieniu od tabeli studentów z `widgetVar="studenci"`). `PF('pokoje')` zwracało `undefined`, wywołanie `.addRow()` rzucało błąd JavaScriptu i PrimeFaces nie kończył obsługi żądania AJAX (Asynchronous JavaScript and XML, czyli żądanie wysyłane w tle bez przeładowania strony). Serwer zrobił swoje, nie zawiodła transakcja. Poprawka: `widgetVar="pokoje"` w `pokoj.xhtml`;
  - znaczniki `<link href="akademik.css">` w nagłówkach poszczególnych stron są martwe: Facelets bierze tylko to, co jest wewnątrz `ui:composition`, a resztę pliku pomija. Usuniemy je przy okazji porządków w XHTML.

**1.7 Plik logu aplikacji we właściwym miejscu.** Log aplikacji będzie trafiał do `logs/akademik.log` w katalogu Tomcata, obok jego własnych logów.

- *Issues:* [#17](https://github.com/szaroblekitny/akademik/issues/17) (closes).
- *Dlaczego plik lądował w `bin`:* ścieżka względna, np. `fileName="akademik.log"`, liczy się od katalogu roboczego procesu, a Tomcat uruchamiany skryptem z `bin` ma tam swój katalog roboczy. Obecnie w `log4j2.xml` zapis do pliku jest w ogóle wykomentowany i wszystko idzie tylko na konsolę.
- *Rozwiązanie:* `fileName="${sys:catalina.base}/logs/akademik.log"`. Log4j wstawi tu właściwość systemową `catalina.base`, którą ustawia Tomcat. Do tego `RollingFile` zamiast `File`, czyli nowy plik co dzień, a stare pliki są z czasem usuwane, więc log nie rośnie bez końca.
- *Do sprawdzenia:* w testach nie ma Tomcata, więc nie ma też `catalina.base`. Trzeba podać wartość domyślną (`${sys:catalina.base:-target}`), żeby testy pisały do `target`.
- *Wynik (8.11.2026):* w `log4j2.xml` są dwie właściwości. `katalogLogow` = `${sys:catalina.base:-target}/logs` (część po `:-` to wartość domyślna), a `wzorzec` to format linii z pełną datą. Doszedł też appender `RollingFile` o nazwie `Plik`: bieżący plik `akademik.log`, a o północy poprzedni dostaje datę w nazwie i jest pakowany do `akademik-RRRR-MM-DD.log.gz`. Spakowane pliki starsze niż 30 dni usuwa `Delete` z `IfLastModified age="P30D"` (zapis okresu w ISO 8601: P = okres, 30D = 30 dni). Kodowanie pliku jest jawnie ustawione na UTF-8, więc polskie znaki nie zależą od strony kodowej Windowsa. `Root` pisze teraz i na konsolę, i do pliku. W testach plik powstaje w `akademik/target/logs`.

**Gotowe, gdy:** wszystkie testy zielone (40 po kroku 1.3: 36 pierwotnych + 4 nowe testy `compareTo`), aplikacja startuje w Tomcacie, każdy ekran się otwiera, a log aplikacji jest w `logs` Tomcata.

**Etap 1 zakończony 8.11.2026.** Wersja aplikacji podniesiona z 0.60-SNAPSHOT na **0.70-SNAPSHOT**, więc WAR nazywa się teraz `akademik##0.70-SNAPSHOT.war`. Gałąź `etap1-porzadki` trafia przez pull request do `master`, a Etap 2 zaczyna się na nowej gałęzi od `master`. W Tomcacie stary WAR i katalog `akademik##0.60-SNAPSHOT` trzeba usunąć ręcznie, bo inaczej Tomcat uruchomi obie wersje naraz (tak działa wdrażanie równoległe).

## Etap 2: import, eksport i raport

Cel: aplikacja robi wszystko, co zapowiada `opis.html`. Większość klocków już istnieje (`Plikowanie`, `Akademik.pobierzZPliku`), trzeba je tylko połączyć z ekranami.

**2.1 Wspólna usługa `DaneService` (warstwa serwisowa).** Logikę „plik → baza” i „baza → plik” przeniesiemy z `Akademik` do osobnej klasy z adnotacją `@Service` i `@Transactional`.

- *Objaśnienie:* bean JSF powinien tylko obsługiwać ekran, a serwis wykonywać pracę w jednej transakcji. Jeśli import 50 studentów padnie na 30., `@Transactional` wycofa wszystko i baza nie zostanie w połowie wypełniona.
- *Nowość:* wstrzykiwanie przez konstruktor zamiast setterów z `@Autowired`. Pola mogą być wtedy `final`, a od Springa 4.3 przy jednym konstruktorze adnotacja `@Autowired` jest zbędna.
- *Wynik (8.11.2026):* nowy pakiet `service` z klasą `DaneService` (`@Service @Transactional`) i rekordem `WynikImportu(int pokoje, int studenci)`. Metody: `importuj(reader)` (rozpoznaje typ obiektu przez `switch` z wzorcami typów z Javy 21), `eksportujPokoje(writer)` i `eksportujStudentow(writer)` (obie `readOnly = true`). `Akademik.pobierzZPliku()` usunięta, a `Akademik.akademik()` woła teraz usługę. Testy: nowy `DaneServiceTest` (4 testy, w tym wycofanie całego importu, gdy trzeci student wskazuje nieistniejący pokój), a dwa stare testy importu z `AkademikTest` przeszły do niego. Razem 42 testy.
- *Faza czerwona:* szkielet usługi zwracał zera. Pierwsze uruchomienie dało 3 błędy zamiast porażek, bo XStream nie umie zapisać listy z `List.of()`: jej klasa `java.util.ImmutableCollections$ListN` jest zamknięta w module `java.base` i refleksja nie ma do niej dostępu. W teście lista jest kopiowana do `ArrayList`. To kolejny argument za 2.1a. Drugie uruchomienie dało dokładnie przewidziane 4 porażki na 44 testy.
- *Pułapki `@Transactional`:* (1) wycofanie następuje domyślnie tylko przy `RuntimeException`, bo kontrolowany `IOException` transakcję zatwierdza; (2) wywołanie metody z wnętrza tej samej klasy omija proxy Springa, a więc i transakcję.

**2.1a JAXB zamiast XStream.** Odczyt i zapis XML w `Plikowanie` przejdzie z XStream na JAXB (Jakarta XML Binding), standard Jakarty. Klasy `Pokoj` i `Student` dostaną adnotacje `@XmlRootElement`, `@XmlElement` itp., a `AkademikXStream` zniknie.

- *Issues:* [#7](https://github.com/szaroblekitny/akademik/issues/7) (closes).
- *Dlaczego:* XStream z pliku XML potrafi utworzyć obiekt **dowolnej** klasy z classpath, jeśli plik o nią poprosi. W aplikacji, która przyjmuje pliki od użytkowników (nasz import), to klasyczna droga ataku. XStream miał wiele takich podatności i dziś chroni się listą dozwolonych klas, którą trzeba pilnować. JAXB tworzy tylko klasy, które sami wskażemy. Przy okazji zniknie ostrzeżenie o `sun.misc.Unsafe` z kroku 1.2b.
- *Kolejność:* przed eksportem (2.2) i importem (2.3), bo oba opierają się na `Plikowanie`. Krok zaczyna się od testu, który zapisuje i odczytuje listę pokoi i studentów. Test przechodzi na XStream, potem musi przejść na JAXB.
- *Decyzja (8.11.2026):* format może się zmienić. Propozycja formatu: schemat `src/main/resources/xsd/akademik.xsd` i przykład `src/test/resources/dane/przyklad.xml`. Główne założenia:
  - jeden korzeń `<akademik>` z opcjonalnymi sekcjami `<pokoje>` i `<studenci>`. Importujemy dokładnie taki plik, jaki wyeksportowaliśmy;
  - brak ID z bazy (#3). Pokój identyfikuje numer, unikalny w pliku (`xs:unique`);
  - **bez przydziału studentów do pokoi.** Plik zawiera tylko dane podstawowe. Kwaterunek jest wynikiem działania aplikacji, a nie daną wejściową, i pokazuje go raport (2.4, 2.5). Pierwsza propozycja miała element `<pokoj>` u studenta. Odrzucona 8.11.2026, bo eksport i import mają być symetryczne;
  - przestrzeń nazw `urn:wojtekz:akademik:dane:1` i atrybut `wersja`, żeby przyszłe zmiany formatu dało się rozpoznać;
  - schemat sprawdzony walidatorem z JDK (`javax.xml.validation`): przykład przechodzi, a duplikat numeru pokoju, nieznana płeć, atrybut `id`, 0 miejsc i `<pokoj>` u studenta są odrzucane.
- *Podział na dwie części (8.11.2026):*
  - **część 1:** zależności `jakarta.xml.bind-api` 4.0.5 i `jaxb-runtime` 4.0.9 (zakres `runtime`). Nowy pakiet `plik` z klasami formatu: `AkademikXml`, `PokojXml`, `StudentXml`, rekord `DaneZPliku` i wyjątek `NieprawidlowyPlikException`. W `Plikowanie` dwie nowe metody: `zapiszDane(OutputStream, ...)` i `wczytajDane(InputStream)`. Odczyt jest sprawdzany schematem `akademik.xsd`, a parser StAX (Streaming API for XML) ma wyłączone DTD i encje zewnętrzne, co chroni przed atakiem XXE (XML External Entity). Nowy test `PlikowanieJaxbTest` ma 4 testy. Faza czerwona: dokładnie przewidziane 4 porażki na 46 testów. XStream w tej części jeszcze zostaje;
  - **część 2:** `DaneService` przechodzi na nowe metody i nadaje ID wczytanym rekordom. Usunięte zostają XStream, `AkademikXStream`, beany `marshaller`/`unmarshaller` i stare testy, a `PlikowanieJaxbTest` zastępuje `PlikowanieTest`.
- *Wynik części 2 (8.11.2026):* `DaneService.importuj(InputStream)` wczytuje cały plik przez `wczytajDane`, nadaje ID (kolejne po `findLastId()`, a przy pustej tabeli od 1; rozwiązanie tymczasowe do kroku 3.3) i zapisuje rekordy. `eksportuj(OutputStream)` zapisuje wszystkie pokoje i wszystkich studentów do jednego pliku; pusta baza daje poprawny plik bez rekordów. Rekord `WynikImportu` nazywa się teraz `LiczbaRekordow`, bo zwraca go też eksport. `Akademik.akademik()` czyta jeden plik zamiast dwóch. Usunięte: zależności `xstream` i `spring-oxm`, klasa `AkademikXStream` z testem, interfejs `Plikowalny`, beany `marshaller`/`unmarshaller` oraz `saveObjectList`/`loadObjectList`. `PlikowanieJaxbTest` nazywa się teraz `PlikowanieTest`. `DaneServiceTest` ma 6 testów (nowe: nadawanie ID i eksport pustej bazy). Faza czerwona: dokładnie przewidziane 5 porażek i 2 błędy na 45 testów, zielona: 45/45.
- *Do ujednolicenia:* limit długości imienia i nazwiska jest różny w trzech miejscach: 500 znaków w schemacie XSD, 255 w testowej bazie HSQLDB (domyślne `varchar(255)` Hibernate'a) i brak limitu w PostgreSQL (`text`). Test wycofania importu celowo wykorzystuje tę różnicę: nazwisko z 300 znaków przechodzi przez schemat, ale nie zapisuje się w bazie.
- *Klasy formatu osobno od encji:* plik nie ma ID ani przydziału pokoi, więc adnotacje JAXB na encjach mieszałyby dwa modele. Klasy XML nie mogą być rekordami, bo JAXB tworzy obiekt konstruktorem bezargumentowym i dopiero potem wypełnia pola.

**2.2 Eksport do XML (P6).** `EksportBean` zamiast żyrafy wygeneruje jeden plik `akademik.xml` z pokojami i studentami w formacie z 2.1a. Format jest taki sam jak przy imporcie, więc eksport i import zrobią pełne koło.

- *Kolejność (zmiana 8.11.2026):* eksport przed importem. Plik wyeksportowany z prawdziwej bazy będzie gotowym materiałem do sprawdzenia importu w 2.3.
- *Objaśnienie:* obecny `EksportBean` liczy plik w konstruktorze, raz na sesję. Przeniesiemy to do metody wywoływanej przy kliknięciu, żeby eksport zawsze pokazywał aktualne dane.
- *Issues:* [#8](https://github.com/szaroblekitny/akademik/issues/8) (refs). Eksport danych zamiast żyrafy. Issue mówi jednak o danych **zakwaterowanych** studentów, a plik eksportu nie zawiera przydziału pokoi (decyzja z 2.1a), więc issue zamknie raport kwaterunku w 2.4.
- *Wynik (8.11.2026):* `EksportBean.getContent()` przy każdym kliknięciu woła `DaneService.eksportuj()` do tablicy bajtów i zwraca `DefaultStreamedContent` z nazwą `akademik.xml` i typem `application/xml`. Bajty zamiast `writer(...)` PrimeFaces, bo eksport kończy się w transakcji usługi, a lambda przechowuje tylko serializowalną tablicę. Konstruktor nie używa już `FacesContext`, a `zyrafa3.jpg` usunięta. Poprawka w `eksport.xhtml`: funkcje `start()`/`stop()` dla `PrimeFaces.monitorDownload` były w `<h:head>` poza `<ui:composition>`, więc Facelets je pomijał; teraz są w `<ui:define>`. Nowy `EksportBeanTest` (2 testy, w tym aktualne dane przy drugim eksporcie w tej samej sesji). Faza czerwona: 1 porażka i 1 błąd na 47, zielona: 47/47. Sprawdzone w Tomcacie: plik z PostgreSQL wygląda dobrze.
- *Ciekawostka:* JAXB z GlassFisha sam dopisuje `standalone="yes"` w deklaracji XML. Oznacza to, że dokument nie zależy od zewnętrznego DTD; z XSD nie ma to nic wspólnego.

**2.3 Import do bazy (P5).** `ImportBean.upload()` przekaże strumień z przesłanego pliku prosto do `DaneService`, bez zapisu na dysk `h:/TEMP/`. Ekran pokaże, ile pokoi lub studentów wczytano.

- *Nowość:* `try-with-resources` (Java 7), czyli `try (InputStream in = ...) { }`. Strumień zamyka się sam, także przy wyjątku. Obecny kod zamyka strumienie ręcznie i przy błędzie zostawia je otwarte.
- *Decyzja (10.11.2026):* na razie import **kasuje** stare dane (studentów, potem pokoje) i wczytuje plik. Przed importem ekran pokazuje okienko potwierdzenia z przypomnieniem, że obecne dane zostaną skasowane. Kasowanie i zapis idą w jednej transakcji `DaneService.importuj`, więc błędny plik albo błąd zapisu zostawia stare dane nietknięte.
- *Issues:* [#3](https://github.com/szaroblekitny/akademik/issues/3) (refs). Import przestanie wymagać pola `id` w plikach. Dopóki nie ma sekwencji (3.3), ID nadaje tymczasowo `DaneService` (od 1, bo tabele są po skasowaniu puste). Issue zamknie dopiero krok 3.3.
- *Wynik (10.11.2026):* `DaneService.importuj` kasuje stare dane przez `deleteAllInBatch` (najpierw studenci, potem pokoje) w tej samej transakcji co zapis. `deleteAllInBatch`, a nie `deleteAll`, bo nowe rekordy dostają te same ID co skasowane, a `deleteAll` zostawia skasowane encje w pamięci Hibernate'a do końca transakcji. `ImportBean.upload()` przekazuje strumień z `try-with-resources` prosto do usługi (bez `h:/TEMP/` i commons-io). Błąd pliku pokazuje treść z walidacji, a inne błędy krótki opis, ze szczegółami w logu. `Akademik.akademik()` nie kasuje już sam. Ekran importu:
  - przycisk „Importuj” sprawdza w przeglądarce, czy wybrano plik; bez pliku pokazuje ostrzeżenie, a z plikiem okienko `p:confirmDialog` z nazwą pliku i przypomnieniem o skasowaniu danych. Sprawdzenie na serwerze zostaje;
  - trzy growle: informacje znikają po 6 s, ostrzeżenia po 10 s, a błędy (`sticky`) zostają do zamknięcia.
  - Testy: nowy `ImportBeanTest` (3 testy z mockiem `UploadedFile`), `DaneServiceTest` ma 7 testów (zastępowanie danych, plik niezgodny ze schematem nie rusza starych danych, wycofanie przywraca też skasowane dane). Faza czerwona: dokładnie przewidziane 3 porażki na 51, zielona: 51/51. Sprawdzone w Tomcacie.

**2.4 Raport XML kwaterunku (P7).** Nowa metoda tworzy dokument XML: lista pokoi, w każdym zakwaterowani studenci, na końcu meldunek o wyniku.

- *Issues:* [#8](https://github.com/szaroblekitny/akademik/issues/8) (closes). To jest „eksport danych o zakwaterowanych studentach”, o który prosi issue. Raport ma własny format, inny niż plik danych z 2.1a, i nie służy do importu.

- *Nowość (Java 16):* `record`, czyli niezmienna klasa danych w jednej linii: `record WierszRaportu(String pokoj, List<String> mieszkancy) {}`. Kompilator sam tworzy konstruktor, gettery, `equals`, `hashCode` i `toString`.
- *Format (10.11.2026):* schemat `src/main/resources/xsd/raport.xsd` i przykład `src/test/resources/dane/raport-przyklad.xml`, zaakceptowane. Własna przestrzeń nazw `urn:wojtekz:akademik:raport:1`, atrybut `utworzono`, pokoje w kolejności numerów z atrybutami `miejsca` i `wolne`, w pokoju studenci w polskiej kolejności nazwisk i imion, opcjonalna sekcja `<niezakwaterowani>` i `<podsumowanie>` z liczbami. Podsumowanie zastąpiło tekstowy meldunek, bo meldunek istnieje tylko chwilę po kwaterowaniu i nie jest nigdzie zapisany, a raport można pobrać później.
- *Wynik (11.11.2026):* nowy pakiet `raport` z rekordami `RaportKwaterunku`, `PokojRaportu` i `StudentRaportu` (konstruktor kompaktowy robi niezmienne kopie list; liczby podsumowania wynikają z list). `RaportService` (`readOnly`) buduje raport z bazy, sortuje przez `Collator` dla języka polskiego i zapisuje XML przez StAX (`XMLStreamWriter`), bo JAXB nie obsługuje rekordów. Data idzie przez formater z sekundami, bo `LocalDateTime.toString()` pomija zerowe sekundy, a `xs:dateTime` ich wymaga. `RaportBean` daje plik `raport-kwaterunku.xml`. Przycisk „Raport XML” jest na stronie głównej (najpierw był na ekranie kwaterowania; przeniesiony 11.11.2026 na prośbę użytkownika, bo tam jest tabela kwaterunku). Testy: `RaportServiceTest` (4) i `RaportBeanTest` (1). Faza czerwona: 2 porażki i 3 błędy na 56, zielona: 56/56. Sprawdzone w Tomcacie.

**2.5 Raport HTML przez XSLT (P7).** XSLT (Extensible Stylesheet Language Transformations) to język opisujący, jak przekształcić jeden dokument XML w inny, np. w HTML. Arkusz `raport.xsl` w zasobach przekształci XML w tabelę HTML. Użyjemy standardowego `javax.xml.transform` z JDK, bez dodatkowych bibliotek. Na stronie głównej, obok przycisku „Raport XML”, pojawi się przycisk „Raport HTML”.

**2.6 Testy.** Każdy krok dostaje test: import z przykładowego XML w `src/test/resources`, eksport i ponowny import dają te same dane, raport zawiera wszystkie pokoje.

**2.7 Sortowanie wyniku na stronie głównej.** Lista na stronie głównej będzie posortowana według pokoi, a w pokoju według nazwisk.

- *Issues:* [#16](https://github.com/szaroblekitny/akademik/issues/16) (closes).
- *Objaśnienie:* kolejność może ustalić baza (metoda repozytorium `findAllByOrderByNumerPokojuAsc()`, którą Spring Data tworzy z samej nazwy metody) albo Java (`Comparator.comparing(Pokoj::getNumerPokoju)`). Wybierzemy jedno i omówimy różnicę. `compareTo` z kroku 1.3 sortuje po ID, a nie po numerze pokoju, więc tu go nie wystarczy użyć wprost.

**Gotowe, gdy:** da się wczytać pliki XML z ekranu, zakwaterować, pobrać raport HTML i wyeksportować dane, a strona główna pokazuje dane posortowane według pokoi.

## Etap 3: lepsze kwaterowanie i identyfikatory

Cel: algorytm mieści więcej studentów, a ID nadaje baza danych. Ten etap robimy ściśle metodą TDD: najpierw test, który pada, potem kod.

**3.1 Test pokazujący słabość algorytmu (P9).** Przykład: pokoje 4-osobowy i 1-osobowy, jedna kobieta i czterech mężczyzn. Obecny algorytm daje kobietę do pokoju 4-osobowego i trzech mężczyzn zostaje bez miejsca. Test będzie wymagał, żeby wszyscy się zmieścili.

**3.2 Nowy algorytm.** Propozycja prostej heurystyki: dla każdej płci policz liczbę osób, a potem przydzielaj pokoje tak, żeby suma miejsc była jak najbliższa potrzebom (najpierw grupa liczniejsza, największe pokoje najpierw). Jeśli się nie zmieszczą, komunikat poda, ilu osób zabrakło.

- *Nowość (Java 8+):* strumienie (`stream()`, `filter`, `sorted`, `collect`) i `Comparator.comparingInt(Pokoj::getLiczbaMiejsc).reversed()` zamiast ręcznych pętli z iteratorami.
- *Nowość (Java 16/21):* zamiast zwracać tekst „Kwaterowanie udane” i porównywać go w `ZakwaterujBean`, zwrócimy `record WynikKwaterowania(boolean udane, int niezakwaterowani, String komunikat)`. Porównywanie napisów jest kruche: literówka psuje logikę bez błędu kompilacji.

**3.3 ID z sekwencji bazy (P8).** `@Id @GeneratedValue(strategy = GenerationType.SEQUENCE)` w encjach i usunięcie `findLastId()`.

- *Dlaczego:* dwóch użytkowników naraz może odczytać ten sam ostatni ID i obaj zapiszą `ID + 1`. Sekwencja w bazie wydaje numery atomowo.
- *Uwaga:* pliki XML z importu zawierają własne ID. Trzeba będzie zdecydować, czy import je zachowuje, czy nadaje nowe.
- *Issues:* [#3](https://github.com/szaroblekitny/akademik/issues/3) (closes). Po tym kroku ID w plikach wejściowych nie są już potrzebne do niczego. Issue chce, żeby program nadawał je sam, więc to przemawia za nadawaniem nowych.
- *Ograniczenia w bazie (dopisane 11.11.2026):* krok i tak zmienia tabele, więc przy okazji `NOT NULL` na kolumnie `student.plec`. Student bez płci nie trafia na listę kobiet ani mężczyzn, więc kwaterowanie go pomija, a eksport i raporty nie przechodzą przez schemat (błąd zgłoszony 11.11.2026, poprawiony w formularzu i w meldunku kwaterowania; baza ma to wykluczyć na stałe, także przy edycji wiersza). Do ujednolicenia także długość imienia i nazwiska (500 w XSD, 255 w HSQLDB, bez limitu w PostgreSQL; patrz 2.1a).

**3.4 Walidacja danych.** Adnotacje Bean Validation (`@NotBlank`, `@Min(1)`) na polach encji, żeby nie dało się zapisać pokoju z 0 miejsc ani studenta bez nazwiska. JSF pokaże błędy automatycznie przy formularzu. Potrzebna będzie implementacja walidacji, czyli zależność Hibernate Validator.

**3.5 Ręczne dodawanie, edycja i kasowanie rekordów.** Ekrany pokoi i studentów dostaną pełną obsługę: dodaj, popraw, usuń. To, co jest dziś na ekranach, sprawdzimy w kroku 1.6 i tu uzupełnimy braki.

- *Ustalenia z 1.6 (8.11.2026):* ekran studentów nie ma przycisku kasowania (pokoje mają). W tym kroku poprawimy też wygląd i zachowanie ekranów: obecny wygląd jest do przemyślenia, a szczegóły ustalimy przed startem kroku. Przy okazji znikną martwe znaczniki `<link href="akademik.css">` z nagłówków stron.

- *Issues:* [#12](https://github.com/szaroblekitny/akademik/issues/12) (closes).
- *Objaśnienie:* w PrimeFaces wygodne są `p:dataTable` z edycją w wierszu (`editable="true"`) i `p:dialog` z formularzem dla nowego rekordu. Walidacja z 3.4 zadziała w obu miejscach bez dodatkowego kodu, a ID nada sekwencja z 3.3, dlatego ten krok jest po nich.
- *Do przemyślenia:* co ma się stać przy kasowaniu pokoju, w którym ktoś mieszka? Proponuję odmowę z komunikatem, bo klucz obcy `student.zakwaterowani` i tak nie pozwoli usunąć takiego pokoju.

**3.6 Generator danych (opcjonalny).** Program pomocniczy tworzy pliki XML z losowymi pokojami i studentami w zadanej liczbie, np. 50 pokoi i 150 studentów.

- *Issues:* [#21](https://github.com/szaroblekitny/akademik/issues/21) (closes).
- *Po co:* do sprawdzania algorytmu na dużych danych i do ręcznych testów importu. Imiona i nazwiska będą losowane z krótkich list, a płeć dopasowana do imienia.
- *Nowość (Java 17):* `RandomGenerator` i `RandomGenerator.of("L64X128MixRandom")`, nowy interfejs generatorów liczb losowych, oraz `random.nextInt(1, 5)` z zakresem od-do. Opcjonalnie `java Generator.java` (Java 11) uruchamia plik źródłowy bez osobnej kompilacji.
- *Kolejność:* krok nie zależy od innych poza formatem XML z 2.1a. Można go zrobić wcześniej, np. przed 3.1, jeśli będzie potrzebny do testów algorytmu.

**Gotowe, gdy:** test z 3.1 przechodzi, nowe rekordy dostają ID z sekwencji, formularze odrzucają błędne dane, a pokoje i studentów da się dodawać, poprawiać i usuwać z ekranu.

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
- *Issues:* [#22](https://github.com/szaroblekitny/akademik/issues/22) (closes). Ustawienia Hibernate'a z `MainConfiguration.hibernateProperties()` trafią do `application.properties` jako `spring.jpa.properties.hibernate.*`. Gdyby Etap 4 miał poczekać, da się to zrobić wcześniej bez Boota: plik `hibernate.properties` w zasobach i `@PropertySource` w konfiguracji.

**4.3 Flyway zamiast luźnych skryptów SQL.** Skrypty z `src/main/sql` staną się migracjami `V1__schemat.sql`, `V2__dane.sql`. Flyway uruchomi je przy starcie aplikacji i zapamięta w tabeli, które już wykonał. Skrypty dla Oracle można usunąć albo zostawić jako osobny zestaw.

- *Issues:* [#23](https://github.com/szaroblekitny/akademik/issues/23) (closes). Kolejna migracja, `V3__nazwy_tabel.sql`, zmieni nazwy: `pokoj` → `pokoje`, `student` → `studenci`, kolumna `zakwaterowani` → `pokoj_id`. Encje dostaną `@Table(name = "pokoje")`, `@Table(name = "studenci")` i `@JoinColumn(name = "pokoj_id")`. Właśnie do takich zmian służy Flyway: istniejąca baza dostaje `ALTER TABLE ... RENAME`, a dane zostają.

**4.4 Testcontainers zamiast HSQLDB.** Testy uruchomią prawdziwy PostgreSQL w kontenerze Dockera. HSQLDB zachowuje się inaczej niż PostgreSQL (typy, sekwencje), więc test może przejść na HSQLDB i paść w produkcji. Wymaga Docker Desktop. *Uwaga:* Dockera obecnie nie ma. Przed tym krokiem trzeba zdecydować, czy go instalować, czy np. testować na lokalnym PostgreSQL w osobnej bazie testowej.

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

| Krok | Opis | Issues | Status | Commit |
| --- | --- | --- | --- | --- |
| 1.1 | Podbicie Mockito (+ ByteBuddy 1.17.7, JaCoCo 0.8.16) |  | zrobione | 1c29a35 |
| 1.2 | Wtyczki Maven i kompilator `release` |  | zrobione | 0b3964e |
| 1.2a | Migracja 10 klas testów na JUnit 5, usunięcie JUnit Vintage |  | zrobione | cdb193b, 04225a9 |
| 1.2b | Ostrzeżenia: statyczny `BeanPostProcessor`, XStream bez `Unsafe` (ostrzeżenie z XStreama zostaje) |  | zrobione | e166b4a |
| 1.3 | Poprawka `compareTo` |  | zrobione (TDD: czerwona 6 porażek, zielona 40/40) | 819a9b2 |
| 1.4 | Dialekt Hibernate (klasa `PostgreSQL10Dialect` nie istniała) |  | zrobione, sprawdzone w Tomcacie w 1.6 | 38cbab1 |
| 1.5 | Przestrzenie nazw JSF, `web.xml` 6.0 |  | zrobione, sprawdzone w Tomcacie w 1.6 | 4521296 |
| 1.6 | Uruchomienie w Tomcacie |  | zrobione: Tomcat 11 + Weld, PostgreSQL 18, wszystkie ekrany przeklikane | 97cd531, e008b10 |
| 1.7 | Plik logu w `logs` Tomcata | #17 | zrobione, sprawdzone w Tomcacie | 72ae1d4 |
| 2.1 | `DaneService` |  | zrobione (TDD: czerwona 4 porażki, zielona 42/42) | eb5a00a |
| 2.1a | JAXB zamiast XStream | #7 | zrobione (część 1: czerwona 4 porażki, zielona 46/46; część 2: czerwona 5 porażek i 2 błędy, zielona 45/45) | 1ac0212 (część 1), bd81a60 (część 2) |
| 2.2 | Eksport do XML | #8 (refs) | zrobione (TDD: czerwona 1 porażka i 1 błąd, zielona 47/47; sprawdzone w Tomcacie) | d565d42 |
| 2.3 | Import do bazy | #3 (refs) | zrobione (TDD: czerwona 3 porażki, zielona 51/51; sprawdzone w Tomcacie) | aa26d8c |
| 2.4 | Raport XML | #8 (closes) | zrobione (TDD: czerwona 2 porażki i 3 błędy, zielona 56/56; sprawdzone w Tomcacie) | 2a9ba29, d690854 (poprawka) |
| 2.5 | Raport HTML przez XSLT |  | do zrobienia | |
| 2.6 | Testy importu, eksportu i raportu |  | do zrobienia | |
| 2.7 | Sortowanie strony głównej | #16 | do zrobienia | |
| 3.1 | Test słabości algorytmu |  | do zrobienia | |
| 3.2 | Nowy algorytm kwaterowania |  | do zrobienia | |
| 3.3 | ID z sekwencji | #3 | do zrobienia | |
| 3.4 | Walidacja danych |  | do zrobienia | |
| 3.5 | Dodawanie, edycja, kasowanie rekordów | #12 | do zrobienia | |
| 3.6 | Generator danych (opcjonalny) | #21 | do zrobienia | |
| 4.1 | Spring 7 i Hibernate 7 |  | do zrobienia | |
| 4.2 | Spring Boot 4 + JoinFaces | #22 | do zrobienia | |
| 4.3 | Flyway | #23 | do zrobienia | |
| 4.4 | Testcontainers |  | do zrobienia | |
| 4.5 | REST API lub Thymeleaf |  | do zrobienia | |
