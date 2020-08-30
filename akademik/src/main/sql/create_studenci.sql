CREATE TABLE public.student
(
    id integer NOT NULL,
    imie text COLLATE pg_catalog."default",
    nazwisko text COLLATE pg_catalog."default",
    plec text COLLATE pg_catalog."default",
    pokoj_id integer,
    CONSTRAINT student_pkey PRIMARY KEY (id),
    CONSTRAINT fk_student_pokoj FOREIGN KEY (pokoj_id)
        REFERENCES public.pokoj (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE public.student
    OWNER to akademik;

COMMENT ON CONSTRAINT fk_student_pokoj ON public.student
    IS 'Pokój, w którym mieszka student';
-- Index: fki_fk_student_pokoj

-- DROP INDEX public.fki_fk_student_pokoj;

CREATE INDEX fki_fk_student_pokoj
    ON public.student USING btree
    (pokoj_id ASC NULLS LAST)
    TABLESPACE pg_default;
