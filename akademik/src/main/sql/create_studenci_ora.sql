CREATE TABLE student
(
    id integer NOT NULL,
    imie varchar2(100),
    nazwisko varchar2(300),
    plec varchar2(15),
    zakwaterowani number,
    CONSTRAINT student_pkey PRIMARY KEY (id)
);