-- Przygotowanie bazy PostgreSQL dla aplikacji Akademik (krok 1.6 planu naprawczego).
--
-- Uruchamiać jako superużytkownik postgres, z katalogu akademik (tego z pom.xml), w PowerShellu:
--   & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -f src\main\sql\setup_postgres.sql
--
-- Co robi:
--   1. zakłada użytkownika (rolę) tomcat i pyta o jego hasło,
--   2. zakłada bazę akademik, której właścicielem jest tomcat,
--   3. w tej bazie, jako tomcat, tworzy tabele i wstawia dane przykładowe
--      z istniejących skryptów w tym katalogu.
-- Skrypt jest jednorazowy: drugie uruchomienie zatrzyma się na CREATE ROLE, bo rola już istnieje.

\encoding UTF8
\set ON_ERROR_STOP on

CREATE ROLE tomcat LOGIN;
\password tomcat
CREATE DATABASE akademik OWNER tomcat ENCODING 'UTF8' TEMPLATE template0;

-- przełączenie na nową bazę; dalej działamy jako tomcat, więc to on jest właścicielem tabel
\connect akademik
SET ROLE tomcat;

\ir create_pokoj.sql
\ir create_student.sql
\ir insert_pokoje.sql
\ir insert_studenci.sql

SELECT count(*) AS pokoje FROM pokoj;
SELECT count(*) AS studenci FROM student;
