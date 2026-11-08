package org.wojtekz.akademik.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
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
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Testy usługi {@link DaneService}: import z XML do bazy, eksport z bazy do XML
 * i wycofanie całego importu, gdy jeden rekord się nie zapisze.
 * <p>
 * Dwa pierwsze testy to dawne {@code AkademikTest.testPobierzPokoje}
 * i {@code testPobierzStudentow}, przeniesione razem z logiką importu.
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
	void testImportuPokoi() throws IOException {
		String xml = doXml(List.of(pokoj(1, "101", 2), pokoj(2, "102", 3)));

		WynikImportu wynik = daneService.importuj(czytnik(xml));

		Assertions.assertEquals(new WynikImportu(2, 0), wynik);
		Assertions.assertEquals(3, pokojRepo.findByNumerPokoju("102").get(0).getLiczbaMiejsc());
	}

	@Test
	void testImportuStudentow() throws IOException {
		String xml = doXml(List.of(
				student(1, "Jan", "Kowalski", Plec.MEZCZYZNA),
				student(3, "Adam", "Malinowski", Plec.MEZCZYZNA),
				student(5, "Jerzy", "Malinowski", Plec.MEZCZYZNA)));

		WynikImportu wynik = daneService.importuj(czytnik(xml));

		Assertions.assertEquals(new WynikImportu(0, 3), wynik);
		Assertions.assertEquals(3, wynik.razem());
		List<Student> malinowscy = studentRepo.findByName("Malinowski");
		Assertions.assertEquals(2, malinowscy.size());
		Assertions.assertEquals("Adam", malinowscy.get(0).getImie());
	}

	/**
	 * Najważniejszy test kroku 2.1: {@code @Transactional}. Trzeci student wskazuje pokój,
	 * którego nie ma w bazie, więc jego zapis się nie uda. Dwóch pierwszych studentów
	 * też nie może zostać w bazie: import ma się udać w całości albo wcale.
	 */
	@Test
	void testImportWycofanyPrzyBledzie() throws IOException {
		Student zlyStudent = student(3, "Ignacy", "Patafian", Plec.MEZCZYZNA);
		zlyStudent.setPokoj(pokoj(99, "999", 1));
		String xml = doXml(List.of(
				student(1, "Jan", "Kowalski", Plec.MEZCZYZNA),
				student(2, "Janina", "Kowalska", Plec.KOBIETA),
				zlyStudent));

		BufferedReader czytnik = czytnik(xml);
		Assertions.assertThrows(RuntimeException.class, () -> daneService.importuj(czytnik));

		Assertions.assertEquals(0, studentRepo.count(), "Import powinien zostać wycofany w całości");
	}

	@Test
	void testEksportuIPonownegoImportu() throws IOException {
		pokojRepo.saveAll(List.of(pokoj(1, "101", 2), pokoj(2, "102", 3)));
		StringWriter tekst = new StringWriter();

		try (BufferedWriter pisarz = new BufferedWriter(tekst)) {
			Assertions.assertEquals(2, daneService.eksportujPokoje(pisarz));
		}
		pokojRepo.deleteAll();
		WynikImportu wynik = daneService.importuj(czytnik(tekst.toString()));

		Assertions.assertEquals(new WynikImportu(2, 0), wynik);
		Assertions.assertEquals(List.of("101", "102"),
				pokojRepo.findAll().stream().map(Pokoj::getNumerPokoju).sorted().toList());
	}

	// ----- pomocnicze -----

	private String doXml(List<?> obiekty) throws IOException {
		StringWriter tekst = new StringWriter();
		try (BufferedWriter pisarz = new BufferedWriter(tekst)) {
			// kopia do ArrayList: XStream nie umie zapisać niezmiennej listy z List.of(),
			// bo jej klasa (java.util.ImmutableCollections$ListN) jest zamknięta w module java.base
			plikowanie.saveObjectList(pisarz, new ArrayList<>(obiekty));
		}
		return tekst.toString();
	}

	private static BufferedReader czytnik(String xml) {
		return new BufferedReader(new StringReader(xml));
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
