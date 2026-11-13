# Audyt kodu i dokumentacji po Etapie 2 planu naprawczego

Przegląd z 11.11.2026, po kroku 2.7 (commit `d61c690`), przed zamknięciem Etapu 2.
Zakres: bezpieczeństwo, optymalność, zasady dobrego programowania, dokumentacja.
Przejrzane: konfiguracja (`pom.xml`, `web.xml`, `context.xml`, konfiguracja Springa, `log4j2.xml`),
wszystkie klasy z `src/main`, strony XHTML, testy, `PLAN.md` i `README.md`.

Każdy punkt ma pola **Decyzja** i **Status**. Decyzja: *zrobić teraz* (przed zamknięciem Etapu 2),
*do planu* (z numerem kroku), *odrzucone* albo *do dyskusji*. Status: *otwarty*, *w toku*, *zrobione*
(z commitem).

Punkty, które proponuję zrobić przed zamknięciem Etapu 2, mają dopisek **(propozycja: teraz)**.

---

## Wysoki priorytet

### A1. Strona błędu pokazuje każdemu pełny stos wywołań (propozycja: teraz)

- **Gdzie:** `src/main/webapp/errors.xhtml`, `web.xml` (`jakarta.faces.PROJECT_STAGE`).
- **Problem:** strona pokazuje `#{pfExceptionHandler.formattedStackTrace}` w każdym trybie.
  - *Ujawnianie informacji:* każdy widzi nazwy klas, wersje bibliotek, fragmenty SQL. To ułatwia atak.
- **Sprostowanie (13.11.2026):** pierwsza wersja audytu zgłaszała też XSS (Cross-Site Scripting)
  przez `escape="false"`. To był fałszywy alarm: PrimeFaces 15 przed wstawieniem `<br/>` w miejsce
  końców wierszy sam escapuje stos (`EscapeUtils.forXml`, sprawdzone w kodzie biblioteki).
  `escape="false"` jest więc potrzebne (inaczej na stronie byłby napis „`<br/>`”) i bezpieczne.
  Komunikat błędu (`#{pfExceptionHandler.message}`) `h:outputText` escapuje domyślnie.
- **Propozycja:** stos tylko w trybie `Development`, wypisywany jak dotąd:
  ```xml
  <h:panelGroup rendered="#{facesContext.application.projectStage eq 'Development'}">
      <p><h:outputText class="szczegolybledu" value="#{pfExceptionHandler.formattedStackTrace}" escape="false" /></p>
  </h:panelGroup>
  ```
  W `Production` stos zniknie sam, zostanie czas i komunikat błędu.
- **Co robi `PROJECT_STAGE`:** w `Development` JSF odświeża zmienione pliki `.xhtml` bez ponownego
  wdrożenia, dodaje ostrzeżenia o pomyłkach (np. niewyświetlony komunikat, przycisk poza `<h:form>`)
  i jest nieco wolniejszy. `Production` jest szybszy, ale zmiana strony wymaga wdrożenia, a pomyłki
  przechodzą po cichu. Sama zmiana trybu nie naprawia tego punktu, bo stos pokazuje nasza strona.
- **Decyzja:** `PROJECT_STAGE` zostaje `Development`, bo projekt jest w fazie rozwoju (11.11.2026).
  Przejście na `Production` przy wersji produkcyjnej, np. w kroku 4.2. Poprawka samej strony
  błędu: *zrobić teraz* (13.11.2026), według propozycji wyżej.
- **Status:** zrobione: stos w `errors.xhtml` w `h:panelGroup` z warunkiem `rendered` na tryb
  `Development`. Sprawdzone w Tomcacie w obu trybach (w `Production` zostają czas i komunikat). Commit `8528be9`.

### A2. Brak logowania użytkowników

- **Problem:** każdy, kto dotrze do adresu w sieci, może jednym importem skasować wszystkie dane
  albo usuwać pokoje.
- **Propozycja:** bezpieczeństwo zarządzane przez Tomcata: `security-constraint`, logowanie
  formularzem, role `kierownik` i `podglad`, hasła jako skrót, HTTPS.
- **Decyzja:** do planu, nowy krok **3.7 Logowanie użytkowników** (11.11.2026).
- **Status:** dopisane do `PLAN.md` (commit z audytem).

### A3. Ekrany Pokoje i Studenci pokazują nieaktualne dane po imporcie (propozycja: teraz)

- **Gdzie:** `PokojBean.getPokoje()`, `StudentBean.getStudenci()`.
- **Problem:** obie metody wczytują listę raz i trzymają ją w sesji („nie odtwarzamy pokoi z bazy
  za każdym razem”). Po imporcie w tej samej sesji ekrany pokazują stare pokoje i studentów, których
  w bazie już nie ma. Edycja takiego wiersza zapisze „ducha” z powrotem do bazy.
- **Propozycja:** pobierać aktualne dane przy każdym wejściu na stronę albo czyścić listę po imporcie
  i po każdej zmianie. Test: import w tej samej sesji, potem lista ma pokazać nowe dane.
- **Decyzja:** zrobić teraz (11.11.2026).
- **Status:** zrobione: `wczytaj()` w `PokojBean` i `StudentBean`, wołane przez `<f:viewAction>` przy każdym wejściu na stronę (GET), a między kliknięciami AJAX lista zostaje w sesji, bo potrzebuje jej edycja wierszy PrimeFaces. Szablon ma miejsce `<ui:insert name="metadata"/>`. Testy: po jednym w `PokojBeanTest` i `StudentBeanTest` (czerwona 2 porażki na 78, zielona 78/78). Sprawdzone w Tomcacie. Commit `d3f0f07`.

---

## Średni priorytet

### B1. Kwaterowanie i opróżnianie bez transakcji

- **Gdzie:** `Akademik.zakwateruj()`, `Akademik.oproznij()`, `ZakwaterujBean.zakwateruj()`.
- **Problem:** każdy student zapisuje się w osobnej transakcji (w logu z 11.11.2026: 14 transakcji
  na jedno kliknięcie). Błąd w połowie zostawi akademik w połowie zakwaterowany; przy większej
  liczbie studentów będzie też wolno.
- **Propozycja:** kwaterowanie w usłudze z `@Transactional` (np. `KwaterunekService`), razem
  z opróżnieniem przed kwaterowaniem.
- **Decyzja:** (propozycja: do planu, krok 3.2)
- **Status:** otwarty.

### B2. `PokojBean`: brak walidacji i dwa błędy

- **Problem:**
  - dodanie pokoju nie sprawdza numeru (maks. 5 znaków, unikalny) ani liczby miejsc (co najmniej 1);
    za długi numer kończy się błędem bazy i stroną błędu, choć według zasady z kroku 2.6 to błąd
    w danych i powinien dać komunikat;
  - `findLastId() + 1L` w `PokojBean` i `StudentBean` daje `NullPointerException` przy pustej
    tabeli: w pustej bazie nie da się ręcznie dodać pierwszego pokoju ani studenta;
  - `deletePokoj()` łapie `UnexpectedRollbackException`, a pokój z mieszkańcami da
    `DataIntegrityViolationException`, więc zamiast komunikatu „pokój ma mieszkańców” będzie
    strona błędu.
- **Propozycja:** walidacja jak przy studentach (komunikat + `required`), poprawne łapanie błędu
  kasowania, ID z sekwencji.
- **Decyzja:** (propozycja: do planu, kroki 3.3, 3.4, 3.5)
- **Status:** otwarty.

### B3. Logi produkcyjne za szczegółowe, z danymi osobowymi (propozycja: teraz)

- **Gdzie:** `log4j2.xml` (`Root level="debug"`), `MainConfiguration.hibernateProperties()`
  (`show_sql`, `format_sql`, `use_sql_comments`).
- **Problem:** logi rosną bardzo szybko i zawierają imiona i nazwiska studentów, czyli dane osobowe
  w rozumieniu RODO.
- **Propozycja:** produkcja na poziomie `info` i bez SQL; szczegółowe logi tylko w testach,
  w osobnym `src/test/resources/log4j2-test.xml` (Log4j sam wybiera go przed `log4j2.xml`).
  Uwaga: dopóki projekt jest w rozwoju, debug może się przydawać; można go włączać ręcznie.
- **Decyzja:**
- **Status:** otwarty.

### B4. `MainConfiguration` połyka błąd JNDI (propozycja: teraz)

- **Gdzie:** `MainConfiguration.entityManagerFactory()`.
- **Problem:** gdy nie ma zasobu `jdbc/akademik`, aplikacja tylko loguje „Błąd szukania bazy danych”
  i startuje dalej bez bazy; błąd wychodzi dopiero przy pierwszym kliknięciu.
- **Propozycja:** „fail fast”: nie łapać `NamingException` (albo opakować i rzucić dalej), żeby
  aplikacja bez bazy nie wystartowała, a przyczyna była od razu w logu Tomcata.
- **Decyzja:**
- **Status:** otwarty.

### B5. Kod testowy w produkcji (propozycja: teraz)

- **Gdzie:** `src/main/java/.../util/DaneTestowe.java`, bean `daneTestowe` w `AkademikConfiguration`.
- **Problem:** klasa wrzucająca dane testowe do bazy jedzie razem z WAR-em i jest beanem
  w produkcyjnym kontekście Springa.
- **Propozycja:** przenieść `DaneTestowe` do `src/test`, a bean do `TestConfiguration`.
- **Decyzja:**
- **Status:** otwarty.

### B6. Atomikos (JTA) przy jednym źródle danych

- **Gdzie:** `MainConfiguration`, zależności `transactions-jta`, `jakarta.jms-api`.
- **Problem:** transakcje rozproszone nie są tu potrzebne; wystarczy `JpaTransactionManager`,
  jak w testach. Atomikos to dodatkowe jary, konfiguracja i szum w logu.
- **Propozycja:** usunąć przy przejściu na Spring Boot.
- **Decyzja:** (propozycja: do planu, krok 4.2; to już otwarta decyzja z planu)
- **Status:** otwarty.

---

## Niski priorytet (porządki)

### C1. Napis „Kwaterowanie udane” porównywany w dwóch klasach

- **Gdzie:** `Akademik`, `ZakwaterujBean`.
- **Propozycja:** stała albo rekord z wynikiem kwaterowania (np. `WynikKwaterowania(boolean udane, String meldunek)`).
- **Decyzja:** (propozycja: do planu, krok 3.2)
- **Status:** otwarty.

### C2. Pozostałości wersji z wiersza poleceń

- **Gdzie:** `Akademik.akademik()`, `Akademik.podajStanAkademika()`.
- **Problem:** `akademik()` łapie `Exception` i tylko loguje (wbrew zasadzie z kroku 2.6); obie
  metody są używane już tylko przez test.
- **Propozycja:** usunąć razem z testem albo zastąpić raportem z kroku 2.4.
- **Decyzja:** (propozycja: do planu, Etap 3)
- **Status:** otwarty.

### C3. Powtórzony komparator z polskim `Collator`

- **Gdzie:** `RaportService.utworzRaport()`, `ZakwaterujBean.getStudenci()`.
- **Propozycja:** jedna klasa pomocnicza z kolejnością polską.
- **Decyzja:**
- **Status:** otwarty.

### C4. `getStudenci()` wywoływane kilka razy przy jednym rysowaniu strony

- **Gdzie:** `ZakwaterujBean.getStudenci()` (strona główna).
- **Problem:** JSF woła getter tabeli kilka razy, więc zapytanie i sortowanie robią się kilka razy.
  Przy obecnej liczbie danych bez znaczenia.
- **Propozycja:** zostawić; ewentualnie lista liczona raz na żądanie.
- **Decyzja:**
- **Status:** otwarty.

### C5. Drobiazgi w kodzie

- `Plec.fromCode` rzuca `UnsupportedOperationException`; właściwy byłby `IllegalArgumentException`;
- pola loggerów (`private static Logger logg`) nie są `final`;
- wcięcia mieszają tabulatory i spacje;
- starsze testy mają publiczne klasy i metody w stylu JUnit 4;
- `AkademikTest` zapisuje pliki do katalogu roboczego zamiast do `@TempDir`.
- **Decyzja:**
- **Status:** otwarty.

### C6. Czcionka z Google Fonts

- **Gdzie:** `template/template.xhtml`.
- **Problem:** zapytanie do zewnętrznego serwera przy każdym wejściu (prywatność) i brak czcionki
  bez internetu.
- **Propozycja:** czcionka w `resources` aplikacji albo czcionka systemowa.
- **Decyzja:** (propozycja: do planu, krok 3.5, wygląd ekranów)
- **Status:** otwarty.

### C7. Wersje bibliotek i narzędzi

- Hibernate 6.6.4, Spring Data JPA 3.4.1 i JUnit 5.11.4 mają nowsze wydania poprawkowe.
- Wtyczki sonar 3.6 (2019), spotbugs 3.1.12 (2019) i jxr 2.3 (2013) są bardzo stare; zaktualizować
  albo usunąć, jeśli nie są używane.
- Reguła JaCoCo wymaga zera klas bez pokrycia, a `MainConfiguration` i `FacesCdiActivator` działają
  tylko w Tomcacie (`haltOnFailure=false`, więc tylko ostrzega). Zapisane też w wyniku kroku 2.6.
- **Decyzja:**
- **Status:** otwarty.

---

## Dokumentacja

### D1. Nieaktualne fragmenty `PLAN.md` (propozycja: teraz)

- krok 2.1a: pierwszy akapit mówi o adnotacjach JAXB na `Pokoj` i `Student`, a powstały osobne
  klasy `PokojXml` i `StudentXml`;
- krok 3.3: zdanie „pliki XML z importu zawierają własne ID” jest nieaktualne od kroku 2.1a.
- **Decyzja:** zrobić teraz (13.11.2026).
- **Status:** zrobione: krok 2.1a opisuje osobne klasy formatu (`AkademikXml`, `PokojXml`,
  `StudentXml`) i odsyła do uzasadnienia; w kroku 3.3 uwaga mówi, że pliki nie mają ID, a import
  zawsze nadaje nowe. Commit `fac8719`.

### D2. README bez instrukcji uruchomienia (propozycja: teraz)

- `README.md` ma 5 zdań. Brakuje: wymagania (JDK 21+, Maven, Tomcat 11, PostgreSQL 18), zasób
  JNDI `jdbc/akademik` w `server.xml`, skrypt `setup_postgres.sql`, `mvn test` i `mvn package`,
  adres aplikacji.
- **Sprostowanie (13.11.2026):** pierwsza wersja punktu podawała Javę 25 (a `pom.xml` ma
  `<release>21</release>`) i sterownik PostgreSQL w `lib` Tomcata (a sterownik jest w WAR-ze
  i tak działa).
- **Decyzja:** zrobić teraz (13.11.2026), README po angielsku.
- **Status:** zrobione: README opisuje aplikację, wymagania, przygotowanie bazy, zasób JNDI
  (z hasłem `CHANGE_ME`), budowę i wdrożenie (`akademik##<wersja>.war`, adres `/akademik/`
  dzięki równoległemu wdrażaniu w Tomcacie) oraz miejsce logu.

### D3. Nieaktualne komentarze w kodzie

- opis klasy `Akademik` („pobiera dane z plików konfiguracyjnych”);
- opis klasy `Messagesy` („bardzo sprytne rozwiązanie skądś ściągnięte”).
- **Decyzja:** zrobić teraz (13.11.2026).
- **Status:** zrobione: `Akademik` opisuje `zakwateruj()` i to, że `akademik(...)` z wersji
  z wiersza poleceń używają już tylko testy; `Messagesy` wyjaśnia, po co jest (atrapa w testach
  zamiast `FacesContext`). Commit `fac8719`.

---

## Co jest dobrze

- ochrona przed XXE (XML External Entity) i walidacja schematem przy imporcie;
- bezpieczny `TransformerFactory` (`FEATURE_SECURE_PROCESSING`, bez zewnętrznych DTD i arkuszy);
- escapowanie danych w raporcie HTML, potwierdzone testem;
- import w jednej transakcji z wycofaniem, także kasowania starych danych;
- wstrzykiwanie przez konstruktor w usługach;
- raport HTML pobierany jako plik, a nie otwierany na stronie aplikacji;
- spójna zasada obsługi błędów (krok 2.6).
