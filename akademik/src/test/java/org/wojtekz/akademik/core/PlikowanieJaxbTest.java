package org.wojtekz.akademik.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.plik.DaneZPliku;
import org.wojtekz.akademik.plik.NieprawidlowyPlikException;

/**
 * Testy zapisu i odczytu plików z danymi przez JAXB (krok 2.1a planu naprawczego).
 * Format opisuje schemat {@code src/main/resources/xsd/akademik.xsd}.
 * <p>
 * W części 2 kroku 2.1a, po usunięciu XStreama, ta klasa zastąpi {@code PlikowanieTest}.
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
class PlikowanieJaxbTest {

	@Autowired
	private Plikowanie plikowanie;

	/** Przykład z {@code src/test/resources/dane}: 4 pokoje i 7 studentów. */
	@Test
	void testWczytaniaPrzykladu() throws IOException {
		DaneZPliku dane;
		try (InputStream plik = getClass().getResourceAsStream("/dane/przyklad.xml")) {
			Assertions.assertNotNull(plik, "Brak pliku /dane/przyklad.xml w zasobach testowych");
			dane = plikowanie.wczytajDane(plik);
		}

		Assertions.assertEquals(4, dane.pokoje().size());
		Assertions.assertEquals(7, dane.studenci().size());

		Pokoj trzeci = dane.pokoje().get(2);
		Assertions.assertEquals("103", trzeci.getNumerPokoju());
		Assertions.assertEquals(3, trzeci.getLiczbaMiejsc());
		Assertions.assertEquals(0, trzeci.getId(), "Plik nie ma ID, więc encja nie powinna go mieć");

		Student drugi = dane.studenci().get(1);
		Assertions.assertEquals("Joanna", drugi.getImie());
		Assertions.assertEquals("Kowalska", drugi.getNazwisko());
		Assertions.assertEquals(Plec.KOBIETA, drugi.getPlec());
		Assertions.assertNull(drugi.getPokoj());

		Student piaty = dane.studenci().get(4);
		Assertions.assertEquals("Jeremi", piaty.getImie());
		Assertions.assertEquals("Kulka", piaty.getNazwisko());
		Assertions.assertEquals(Plec.MEZCZYZNA, piaty.getPlec());
		Assertions.assertNull(piaty.getPokoj());
	}

	/** Eksport i ponowny import dają te same dane, a zapisany plik spełnia schemat. */
	@Test
	void testZapisuIOdczytu() {
		List<Pokoj> pokoje = List.of(pokoj(7, "101", 2), pokoj(8, "103A", 4));
		List<Student> studenci = List.of(student(5, "Łucja", "Żółć", Plec.KOBIETA));

		ByteArrayOutputStream bajty = new ByteArrayOutputStream();
		plikowanie.zapiszDane(bajty, pokoje, studenci);
		String xml = bajty.toString(StandardCharsets.UTF_8);

		Assertions.assertTrue(xml.contains("urn:wojtekz:akademik:dane:1"), "Brak przestrzeni nazw:\n" + xml);
		Assertions.assertFalse(xml.contains("id="), "Plik nie powinien zawierać ID:\n" + xml);

		// wczytajDane sprawdza schemat, więc przejście tej linii oznacza też zgodność ze schematem
		DaneZPliku dane = plikowanie.wczytajDane(new ByteArrayInputStream(bajty.toByteArray()));

		Assertions.assertEquals(List.of("101", "103A"),
				dane.pokoje().stream().map(Pokoj::getNumerPokoju).toList());
		Assertions.assertEquals(List.of(2, 4),
				dane.pokoje().stream().map(Pokoj::getLiczbaMiejsc).toList());
		Student lucja = dane.studenci().get(0);
		Assertions.assertEquals("Łucja", lucja.getImie(), "Polskie znaki muszą przetrwać zapis i odczyt");
		Assertions.assertEquals("Żółć", lucja.getNazwisko());
		Assertions.assertEquals(Plec.KOBIETA, lucja.getPlec());
	}

	/** Plik niezgodny ze schematem: dwa pokoje o tym samym numerze. */
	@Test
	void testOdrzuceniaPlikuNiezgodnegoZeSchematem() {
		String xml = """
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <pokoje>
				    <pokoj numer="101" miejsca="1"/>
				    <pokoj numer="101" miejsca="2"/>
				  </pokoje>
				</akademik>
				""";

		NieprawidlowyPlikException blad = Assertions.assertThrows(NieprawidlowyPlikException.class,
				() -> plikowanie.wczytajDane(strumien(xml)));

		Assertions.assertTrue(blad.getMessage().contains("101"),
				"Komunikat powinien wskazać powtórzony numer, jest: " + blad.getMessage());
	}

	/**
	 * Ochrona przed atakiem XXE (XML External Entity): plik deklaruje „encję zewnętrzną”,
	 * która miałaby wczytać do danych zawartość pliku z dysku serwera. Taki plik ma być
	 * odrzucony, zanim parser w ogóle spróbuje sięgnąć po ten plik.
	 */
	@Test
	void testOdrzuceniaEncjiZewnetrznej() {
		String xml = """
				<?xml version="1.0" encoding="UTF-8"?>
				<!DOCTYPE akademik [ <!ENTITY sekret SYSTEM "file:///c:/Windows/win.ini"> ]>
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <studenci>
				    <student plec="KOBIETA"><imie>&sekret;</imie><nazwisko>X</nazwisko></student>
				  </studenci>
				</akademik>
				""";

		Assertions.assertThrows(NieprawidlowyPlikException.class, () -> plikowanie.wczytajDane(strumien(xml)));
	}

	// ----- pomocnicze -----

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
