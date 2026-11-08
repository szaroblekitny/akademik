package org.wojtekz.akademik.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.core.Plikowanie;
import org.wojtekz.akademik.entity.Plikowalny;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Usługa przenosząca dane między plikami XML a bazą: import (plik → baza)
 * i eksport (baza → plik). Beany JSF obsługują ekran, a pracę zlecają tej usłudze.
 * <p>
 * {@link Transactional} na klasie: każda publiczna metoda wywołana z zewnątrz działa
 * w jednej transakcji. Spring nie zmienia tej klasy, tylko podaje innym beanom
 * <i>proxy</i>, czyli obiekt pośredniczący, który przed metodą otwiera transakcję,
 * a po niej ją zatwierdza albo, gdy poleciał wyjątek, wycofuje.
 * <p>
 * Uwaga na dwie pułapki {@code @Transactional}:
 * <ul>
 * <li>domyślnie wycofanie następuje tylko przy wyjątkach niekontrolowanych
 *     ({@code RuntimeException}, {@code Error}). Kontrolowany {@code IOException}
 *     transakcję <b>zatwierdza</b>. U nas {@code IOException} może polecieć tylko
 *     przy czytaniu pliku, zanim cokolwiek zapiszemy, więc to nie szkodzi;</li>
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
	 * Czyta listę obiektów z pliku XML i zapisuje je w bazie, rozpoznając typ każdego
	 * obiektu. Import udaje się w całości albo wcale: jeśli któryś rekord się nie
	 * zapisze, transakcja wycofa także rekordy zapisane przed nim.
	 *
	 * @param reader źródło danych XML
	 * @return ile pokoi i ilu studentów zapisano
	 * @throws IOException błąd odczytu
	 * @throws IllegalArgumentException gdy w pliku jest obiekt nieznanego typu
	 */
	public WynikImportu importuj(BufferedReader reader) throws IOException {
		List<Plikowalny> pobrane = plikowanie.loadObjectList(reader);
		logg.debug("----->>> importuj: odczytano {} obiektów", pobrane.size());

		int pokoje = 0;
		int studenci = 0;

		for (Plikowalny obiekt : pobrane) {
			// switch z wzorcami typów (Java 21): rozpoznaje klasę i od razu daje
			// zmienną właściwego typu, bez instanceof i rzutowania
			switch (obiekt) {
				case Pokoj pokoj -> {
					pokojRepo.save(pokoj);
					pokoje++;
				}
				case Student student -> {
					studentRepo.save(student);
					studenci++;
				}
				default -> throw new IllegalArgumentException(
						"Nieznany typ obiektu w pliku: " + obiekt.getClass().getName());
			}
		}

		WynikImportu wynik = new WynikImportu(pokoje, studenci);
		logg.info("----->>> importuj: {}", wynik);
		return wynik;
	}

	/**
	 * Zapisuje wszystkie pokoje z bazy do pliku XML.
	 * {@code readOnly = true}: transakcja tylko do odczytu, Hibernate nie musi
	 * śledzić zmian w obiektach.
	 *
	 * @param writer cel zapisu
	 * @return liczba zapisanych pokoi
	 * @throws IOException błąd zapisu albo pusta baza
	 */
	@Transactional(readOnly = true)
	public int eksportujPokoje(BufferedWriter writer) throws IOException {
		List<Pokoj> pokoje = pokojRepo.findAll();
		plikowanie.saveObjectList(writer, pokoje);
		return pokoje.size();
	}

	/**
	 * Zapisuje wszystkich studentów z bazy do pliku XML.
	 *
	 * @param writer cel zapisu
	 * @return liczba zapisanych studentów
	 * @throws IOException błąd zapisu albo pusta baza
	 */
	@Transactional(readOnly = true)
	public int eksportujStudentow(BufferedWriter writer) throws IOException {
		List<Student> studenci = studentRepo.findAll();
		plikowanie.saveObjectList(writer, studenci);
		return studenci.size();
	}
}
