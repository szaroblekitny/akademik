package org.wojtekz.akademik.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.core.Plikowanie;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.plik.DaneZPliku;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Usługa przenosząca dane między plikiem XML a bazą: import (plik → baza)
 * i eksport (baza → plik). Plik ma format {@code /xsd/akademik.xsd}: jeden
 * dokument z pokojami i studentami, bez ID i bez przydziału pokoi.
 * Beany JSF obsługują ekran, a pracę zlecają tej usłudze.
 * <p>
 * {@link Transactional} na klasie: każda publiczna metoda wywołana z zewnątrz działa
 * w jednej transakcji. Spring nie zmienia tej klasy, tylko podaje innym beanom
 * <i>proxy</i>, czyli obiekt pośredniczący, który przed metodą otwiera transakcję,
 * a po niej ją zatwierdza albo, gdy poleciał wyjątek, wycofuje.
 * <p>
 * Uwaga na dwie pułapki {@code @Transactional}:
 * <ul>
 * <li>domyślnie wycofanie następuje tylko przy wyjątkach niekontrolowanych
 *     ({@code RuntimeException}, {@code Error}). U nas błędy pliku to
 *     {@code NieprawidlowyPlikException}, a błędy bazy to {@code DataAccessException}
 *     Springa. Oba dziedziczą po {@code RuntimeException}, więc oba wycofują transakcję;</li>
 * <li>wywołanie metody z wnętrza tej samej klasy ({@code this.importuj(...)})
 *     omija proxy, a więc i transakcję.</li>
 * </ul>
 * <p>
 * Zależności przychodzą przez konstruktor (pola {@code final}). Przy jednym
 * konstruktorze Spring (od wersji 4.3) wstrzykuje je bez adnotacji {@code @Autowired}.
 *
 * @author Wojciech Zaręba
 */
@Service
@Transactional
public class DaneService {
	private static final Logger logg = LogManager.getLogger();

	private final Plikowanie plikowanie;
	private final PokojRepository pokojRepo;
	private final StudentRepository studentRepo;

	public DaneService(Plikowanie plikowanie, PokojRepository pokojRepo, StudentRepository studentRepo) {
		this.plikowanie = plikowanie;
		this.pokojRepo = pokojRepo;
		this.studentRepo = studentRepo;
	}

	/**
	 * Wczytuje plik z danymi i zastępuje nim dane w bazie: kasuje wszystkich
	 * studentów i wszystkie pokoje, a potem zapisuje rekordy z pliku (decyzja z kroku 2.3).
	 * Import udaje się w całości albo wcale: kasowanie i zapis są w jednej transakcji,
	 * więc gdy któryś rekord się nie zapisze, w bazie zostają stare dane.
	 * <p>
	 * Plik nie zawiera ID, więc nadaje je ta metoda. Tabele są po skasowaniu puste,
	 * więc numeracja zaczyna się od 1. To rozwiązanie tymczasowe: krok 3.3 zastąpi je
	 * sekwencją w bazie.
	 *
	 * @param in plik z danymi; metoda go nie zamyka
	 * @return ile pokoi i ilu studentów zapisano
	 * @throws org.wojtekz.akademik.plik.NieprawidlowyPlikException gdy plik nie spełnia schematu
	 */
	public LiczbaRekordow importuj(InputStream in) {
		// najpierw cały plik: błąd formatu wychodzi, zanim cokolwiek skasujemy
		DaneZPliku dane = plikowanie.wczytajDane(in);

		// Najpierw studenci, bo wskazują pokoje (klucz obcy). deleteAllInBatch wysyła
		// jedno polecenie DELETE od razu, bez wczytywania encji. Zwykłe deleteAll
		// zostawiłoby skasowane encje w pamięci Hibernate'a do końca transakcji,
		// a nowe rekordy dostają te same ID (od 1) i Hibernate uznałby je za skasowane.
		studentRepo.deleteAllInBatch();
		pokojRepo.deleteAllInBatch();

		long idPokoju = 1;
		for (Pokoj pokoj : dane.pokoje()) {
			pokoj.setId(idPokoju++);
		}
		long idStudenta = 1;
		for (Student student : dane.studenci()) {
			student.setId(idStudenta++);
		}

		pokojRepo.saveAll(dane.pokoje());
		studentRepo.saveAll(dane.studenci());

		LiczbaRekordow wynik = new LiczbaRekordow(dane.pokoje().size(), dane.studenci().size());
		logg.info("----->>> Zaimportowano: {}", wynik);
		return wynik;
	}

	/**
	 * Zapisuje wszystkie pokoje i wszystkich studentów z bazy do jednego pliku.
	 * {@code readOnly = true}: transakcja tylko do odczytu, Hibernate nie musi
	 * śledzić zmian w obiektach. Pusta baza daje poprawny plik bez rekordów.
	 *
	 * @param out cel zapisu; metoda go nie zamyka
	 * @return ile pokoi i ilu studentów zapisano do pliku
	 */
	@Transactional(readOnly = true)
	public LiczbaRekordow eksportuj(OutputStream out) {
		// schemat pliku wymaga płci; bez tego sprawdzenia eksport padał na walidacji
		// z komunikatem zrozumiałym tylko dla programisty
		NiekompletneDaneException.sprawdzPlec(studentRepo.findByPlecIsNull());
		List<Pokoj> pokoje = pokojRepo.findAll();
		List<Student> studenci = studentRepo.findAll();
		plikowanie.zapiszDane(out, pokoje, studenci);

		LiczbaRekordow wynik = new LiczbaRekordow(pokoje.size(), studenci.size());
		logg.info("----->>> Wyeksportowano: {}", wynik);
		return wynik;
	}
}
