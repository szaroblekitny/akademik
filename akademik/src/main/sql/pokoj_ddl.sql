CREATE TABLE public.pokoj (
    id            integer CONSTRAINT pokojkey PRIMARY KEY,
    numer_pokoju  varchar(5),
    liczba_miejsc integer
);
