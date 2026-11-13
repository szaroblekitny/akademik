Akademik
========

A simple JSF (Jakarta Faces) application that assigns dormitory rooms to students
automatically. Women and men are housed in separate rooms. I use it to learn Spring and
other Java technologies. The user interface and the code comments are in Polish :-)

Features: managing rooms and students, automatic room assignment, import and export of
data as XML, and a room assignment report in XML or HTML.

Technologies: Jakarta EE 11 (Faces 4.1/Mojarra, CDI/Weld), PrimeFaces 15, Spring 6.2
with Spring Data JPA, Hibernate 6.6, JTA transactions (Atomikos), PostgreSQL;
HSQLDB in tests.

Requirements
------------

- JDK 21 or newer
- Maven 3.9+
- Apache Tomcat 11 (Tomcat 10.1 is not enough: the application needs Jakarta EE 11)
- PostgreSQL (tested with version 18)

Setup
-----

### 1. Database

Run the setup script once as the `postgres` superuser, from the `akademik` directory
(the one with `pom.xml`). It creates the `tomcat` role (and asks for its password), the
`akademik` database and the tables with sample data:

    psql -U postgres -f src/main/sql/setup_postgres.sql

On Windows, run `chcp 1250` first so that Polish characters display correctly.

### 2. Tomcat data source

Add a global JNDI resource to `<GlobalNamingResources>` in Tomcat's `conf/server.xml`.
Use the password you set for the `tomcat` role:

```xml
<Resource name="jdbc/akademik" auth="Container"
          type="javax.sql.DataSource"
          driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://localhost:5432/akademik"
          username="tomcat" password="CHANGE_ME"
          maxTotal="10" maxIdle="5" maxWaitMillis="10000" />
```

The application links to this resource in `META-INF/context.xml`. The PostgreSQL JDBC
driver is included in the WAR. If Tomcat reports that it cannot load
`org.postgresql.Driver`, copy the driver JAR to Tomcat's `lib` directory.

### 3. Build and deploy

    mvn test       # unit tests on an in-memory HSQLDB database, no PostgreSQL needed
    mvn package    # builds target/akademik##<version>.war

Copy the WAR to Tomcat's `webapps` directory and start Tomcat. The application runs at
<http://localhost:8080/akademik/>. Tomcat treats the part after `##` as the application
version, not as part of the address (parallel deployment). When you copy a newer version
next to the old one, new sessions go to the newer version, while existing sessions finish
on the old one.

The application log goes to `logs/akademik.log` in the Tomcat directory.

Development notes
-----------------

The ongoing refactoring plan, with a progress log, is in [PLAN.md](PLAN.md) (in Polish).

License
-------

The project is under Simple Public License 2.0 (see [LICENSE.TXT](LICENSE.TXT)).
