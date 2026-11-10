package org.wojtekz.akademik.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.core.Plikowanie;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.plik.DaneZPliku;
import org.wojtekz.akademik.plik.NieprawidlowyPlikException;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Testy usługi {@link DaneService}: import pliku do bazy w miejsce starych danych,
 * z nadaniem ID, eksport bazy do pliku i wycofanie całego importu (razem z kasowaniem
 * starych danych), gdy jeden rekord się nie zapisze.
 * <p>
 * Pliki do importu są zapisane wprost w testach jako bloki tekstu, żeby było widać
 * format {@code akademik.xsd}. Eksport sprawdzamy, czytając jego wynik z powrotem.
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
class DaneServiceTest {

	@Autowired
	private DaneService daneService;

	@Autowired
	private Plikowanie plikowanie;

	@Autowired
	private PokojRepository pokojRepo;

	@Autowired
	private StudentRepository studentRepo;

	@BeforeEach
	@AfterEach
	void wyczyscBaze() {
		// baza HSQLDB w pamięci jest wspólna dla wszystkich klas testowych
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
	}

	@Test
	void testImportuPokoi() {
		String xml = """
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <pokoje>
				    <pokoj numer="101" miejsca="2"/>
				    <pokoj numer="102" miejsca="3"/>
				  </pokoje>
				</akademik>
				""";

		LiczbaRekordow wynik = daneService.importuj(strumien(xml));

		Assertions.assertEquals(new LiczbaRekordow(2, 0), wynik);
		Assertions.assertEquals(3, pokojRepo.findByNumerPokoju("102").get(0).getLiczbaMiejsc());
	}

	@Test
	void testImportuStudentow() {
		String xml = """
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <studenci>
				    <student plec="MEZCZYZNA"><imie>Jan</imie><nazwisko>Kowalski</nazwisko></student>
				    <student plec="MEZCZYZNA"><imie>Adam</imie><nazwisko>Malinowski</nazwisko></student>
				    <student plec="MEZCZYZNA"><imie>Jerzy</imie><nazwisko>Malinowski</nazwisko></student>
				  </studenci>
				</akademik>
				""";

		LiczbaRekordow wynik = daneService.importuj(strumien(xml));

		Assertions.assertEquals(new LiczbaRekordow(0, 3), wynik);
		Assertions.assertEquals(3, wynik.razem());
		List<Student> malinowscy = studentRepo.findByName("Malinowski");
		Assertions.assertEquals(2, malinowscy.size());
		Assertions.assertEquals("Adam", malinowscy.get(0).getImie());
	}

	/**
	 * Import zastępuje dane (decyzja z kroku 2.3): stary pokój i stary student znikają,
	 * a ID nadawane są od nowa, od 1, w kolejności z pliku. Student mieszka w starym
	 * pokoju, więc kasowanie musi iść w dobrej kolejności: najpierw studenci, potem pokoje.
	 */
	@Test
	void testImportZastepujeDane() {
		zapiszStareDane();
		String xml = """
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <pokoje>
				    <pokoj numer="201" miejsca="2"/>
				    <pokoj numer="202" miejsca="2"/>
				  </pokoje>
				  <studenci>
				    <student plec="KOBIETA"><imie>Ola</imie><nazwisko>Lis</nazwisko></student>
				  </studenci>
				</akademik>
				""";

		LiczbaRekordow wynik = daneService.importuj(strumien(xml));

		Assertions.assertEquals(new LiczbaRekordow(2, 1), wynik);
		Assertions.assertEquals(2, pokojRepo.count(), "Zostają tylko pokoje z pliku");
		Assertions.assertTrue(pokojRepo.findByNumerPokoju("100").isEmpty());
		Assertions.assertTrue(studentRepo.findByName("Nowak").isEmpty());
		Assertions.assertEquals(1, pokojRepo.findByNumerPokoju("201").get(0).getId());
		Assertions.assertEquals(2, pokojRepo.findByNumerPokoju("202").get(0).getId());
		Assertions.assertEquals(1, studentRepo.findByName("Lis").get(0).getId());
	}

	/** Plik niezgodny ze schematem: wyjątek, a stare dane zostają nietknięte. */
	@Test
	void testNieprawidlowyPlikZostawiaDane() {
		zapiszStareDane();
		String xml = """
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <pokoje>
				    <pokoj numer="201" miejsca="0"/>
				  </pokoje>
				</akademik>
				""";

		InputStream plik = strumien(xml);
		Assertions.assertThrows(NieprawidlowyPlikException.class, () -> daneService.importuj(plik));

		Assertions.assertEquals(1, pokojRepo.count());
		Assertions.assertEquals(1, studentRepo.count());
	}

	/**
	 * Najważniejszy test kroku 2.1: {@code @Transactional}. Trzeci student ma nazwisko
	 * z 300 znaków. Schemat pliku je przepuszcza (do 500), ale testowa baza HSQLDB nie:
	 * Hibernate założył kolumnę {@code varchar(255)}, bo encja nie podaje długości.
	 * Zapis trzeciego studenta się więc nie uda, a dwóch pierwszych też nie może zostać
	 * w bazie: import ma się udać w całości albo wcale.
	 * <p>
	 * Od kroku 2.3 import najpierw kasuje stare dane. Kasowanie jest w tej samej
	 * transakcji, więc też musi zostać wycofane: stary student ma przetrwać.
	 */
	@Test
	void testImportWycofanyPrzyBledzie() {
		zapiszStareDane();
		String xml = """
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <studenci>
				    <student plec="MEZCZYZNA"><imie>Jan</imie><nazwisko>Kowalski</nazwisko></student>
				    <student plec="KOBIETA"><imie>Janina</imie><nazwisko>Kowalska</nazwisko></student>
				    <student plec="MEZCZYZNA"><imie>Ignacy</imie><nazwisko>%s</nazwisko></student>
				  </studenci>
				</akademik>
				""".formatted("P".repeat(300));

		InputStream plik = strumien(xml);
		Assertions.assertThrows(RuntimeException.class, () -> daneService.importuj(plik));

		Assertions.assertEquals(1, studentRepo.count(), "Import powinien zostać wycofany w całości");
		Assertions.assertEquals("Ewa", studentRepo.findByName("Nowak").get(0).getImie(),
				"Kasowanie starych danych też powinno zostać wycofane");
		Assertions.assertEquals(1, pokojRepo.count());
	}

	/**
	 * Pełne koło: eksport, wyczyszczenie bazy, import tego samego pliku.
	 * Przydział do pokoju nie przechodzi przez plik (decyzja z kroku 2.1a).
	 */
	@Test
	void testEksportuIPonownegoImportu() {
		Pokoj pokoj101 = pokoj(1, "101", 2);
		pokojRepo.saveAll(List.of(pokoj101, pokoj(2, "102", 3)));
		Student kowalski = student(1, "Jan", "Kowalski", Plec.MEZCZYZNA);
		kowalski.setPokoj(pokoj101);
		studentRepo.saveAll(List.of(kowalski, student(2, "Łucja", "Żółć", Plec.KOBIETA)));
		ByteArrayOutputStream bajty = new ByteArrayOutputStream();

		Assertions.assertEquals(new LiczbaRekordow(2, 2), daneService.eksportuj(bajty));

		wyczyscBaze();
		LiczbaRekordow wynik = daneService.importuj(new ByteArrayInputStream(bajty.toByteArray()));

		Assertions.assertEquals(new LiczbaRekordow(2, 2), wynik);
		Assertions.assertEquals(List.of("101", "102"),
				pokojRepo.findAll().stream().map(Pokoj::getNumerPokoju).sorted().toList());
		Student lucja = studentRepo.findByName("Żółć").get(0);
		Assertions.assertEquals("Łucja", lucja.getImie());
		Assertions.assertEquals(Plec.KOBIETA, lucja.getPlec());
		Assertions.assertNull(studentRepo.findByName("Kowalski").get(0).getPokoj(),
				"Plik nie przenosi przydziału do pokoju");
	}

	/** Pusta baza daje poprawny plik bez rekordów, a nie błąd jak dawniej w XStreamie. */
	@Test
	void testEksportuPustejBazy() {
		ByteArrayOutputStream bajty = new ByteArrayOutputStream();

		Assertions.assertEquals(new LiczbaRekordow(0, 0), daneService.eksportuj(bajty));

		DaneZPliku dane = plikowanie.wczytajDane(new ByteArrayInputStream(bajty.toByteArray()));
		Assertions.assertTrue(dane.pokoje().isEmpty());
		Assertions.assertTrue(dane.studenci().isEmpty());
	}

	// ----- pomocnicze -----

	/** Stary pokój 100 (ID 5) i mieszkająca w nim Ewa Nowak (ID 9). */
	private void zapiszStareDane() {
		Pokoj stary = pokoj(5, "100", 1);
		pokojRepo.save(stary);
		Student ewa = student(9, "Ewa", "Nowak", Plec.KOBIETA);
		ewa.setPokoj(stary);
		studentRepo.save(ewa);
	}

	private static InputStream strumien(String xml) {
		return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
	}

	private static Pokoj pokoj(long id, String numer, int miejsc) {
		Pokoj pokoj = new Pokoj();
		pokoj.setId(id);
		pokoj.setNumerPokoju(numer);
		pokoj.setLiczbaMiejsc(miejsc);
		return pokoj;
	}

	private static Student student(long id, String imie, String nazwisko, Plec plec) {
		Student student = new Student();
		student.setId(id);
		student.setImie(imie);
		student.setNazwisko(nazwisko);
		student.setPlec(plec);
		return student;
	}
}
