CREATE TABLE public.student
(
    id integer NOT NULL,
    imie text COLLATE pg_catalog."default",
    nazwisko text COLLATE pg_catalog."default",
    plec text COLLATE pg_catalog."default",
    zakwaterowani integer,
    CONSTRAINT student_pkey PRIMARY KEY (id),
    CONSTRAINT fk_student_pokoj FOREIGN KEY (zakwaterowani)
        REFERENCES public.pokoj (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;


