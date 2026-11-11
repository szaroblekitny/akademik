package org.wojtekz.akademik.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.core.Akademik;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.raport.PokojRaportu;
import org.wojtekz.akademik.raport.RaportKwaterunku;
import org.wojtekz.akademik.raport.StudentRaportu;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
import org.xml.sax.SAXException;

/**
 * Testy raportu kwaterunku (krok 2.4 planu naprawczego). Dane przychodzą
 * z importu prawdziwego pliku, a kwaterunek robi prawdziwy {@link Akademik}.
 * XML raportu sprawdzamy schematem {@code /xsd/raport.xsd}.
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
class RaportServiceTest {

	@Autowired
	private RaportService raportService;

	@Autowired
	private DaneService daneService;

	@Autowired
	private Akademik akademik;

	@Autowired
	private PokojRepository pokojRepo;

	@Autowired
	private StudentRepository studentRepo;

	@BeforeEach
	@AfterEach
	void wyczyscBaze() {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
	}

	/**
	 * Przykład z zasobów (4 pokoje, 7 studentów) po kwaterowaniu: wszyscy mają pokój.
	 * Kolejność: pokoje według numerów, w pokoju studenci według nazwisk.
	 */
	@Test
	void testRaportuPoKwaterowaniu() throws IOException {
		importujPrzyklad();
		akademik.zakwateruj();

		RaportKwaterunku raport = raportService.utworzRaport();

		Assertions.assertEquals(List.of("101", "102", "103", "104"),
				raport.pokoje().stream().map(PokojRaportu::numer).toList());
		PokojRaportu pokoj102 = raport.pokoje().get(1);
		Assertions.assertEquals(List.of(new StudentRaportu("Janina", "Kaptur", Plec.KOBIETA)), pokoj102.mieszkancy());
		Assertions.assertEquals(1, pokoj102.wolne(), "Mężczyźni nie mieszkają z kobietami, więc jedno miejsce zostaje");
		Assertions.assertEquals(List.of("Kapucyn", "Kowal", "Kowalski"),
				raport.pokoje().get(2).mieszkancy().stream().map(StudentRaportu::nazwisko).toList());
		Assertions.assertTrue(raport.niezakwaterowani().isEmpty());
		Assertions.assertEquals(4, raport.pokoi());
		Assertions.assertEquals(10, raport.miejsc());
		Assertions.assertEquals(7, raport.studentow());
		Assertions.assertEquals(7, raport.zakwaterowanych());
	}

	/**
	 * Za mało miejsc: jeden pokój na trzech studentów. Ci bez pokoju trafiają do osobnej
	 * listy, w polskiej kolejności nazwisk („Ś” po „S”, a nie na końcu alfabetu).
	 */
	@Test
	void testNiezakwaterowanych() {
		daneService.importuj(strumien("""
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <pokoje>
				    <pokoj numer="1" miejsca="1"/>
				  </pokoje>
				  <studenci>
				    <student plec="MEZCZYZNA"><imie>Adam</imie><nazwisko>Zając</nazwisko></student>
				    <student plec="MEZCZYZNA"><imie>Piotr</imie><nazwisko>Świerk</nazwisko></student>
				    <student plec="MEZCZYZNA"><imie>Jan</imie><nazwisko>Sowa</nazwisko></student>
				  </studenci>
				</akademik>
				"""));
		akademik.zakwateruj();

		RaportKwaterunku raport = raportService.utworzRaport();

		Assertions.assertEquals(List.of("Zając"),
				raport.pokoje().get(0).mieszkancy().stream().map(StudentRaportu::nazwisko).toList());
		Assertions.assertEquals(List.of("Sowa", "Świerk"),
				raport.niezakwaterowani().stream().map(StudentRaportu::nazwisko).toList());
		Assertions.assertEquals(3, raport.studentow());
		Assertions.assertEquals(1, raport.zakwaterowanych());
	}

	/** XML raportu spełnia schemat i zawiera dane z bazy (z polskimi znakami). */
	@Test
	void testZapisuXmlZgodnegoZeSchematem() throws IOException, SAXException {
		importujPrzyklad();
		akademik.zakwateruj();
		ByteArrayOutputStream bajty = new ByteArrayOutputStream();

		RaportKwaterunku raport = raportService.zapiszRaport(bajty);

		sprawdzSchematem(bajty.toByteArray());
		String xml = bajty.toString(StandardCharsets.UTF_8);
		Assertions.assertTrue(xml.contains(RaportService.PRZESTRZEN_NAZW), "Brak przestrzeni nazw:\n" + xml);
		Assertions.assertTrue(xml.contains("<nazwisko>Kaptur</nazwisko>"), xml);
		Assertions.assertTrue(xml.contains("zakwaterowanych=\"7\""), xml);
		Assertions.assertEquals(7, raport.zakwaterowanych());
	}

	/** Pusta baza: poprawny raport z samym podsumowaniem, bez sekcji niezakwaterowanych. */
	@Test
	void testRaportuPustejBazy() throws IOException, SAXException {
		ByteArrayOutputStream bajty = new ByteArrayOutputStream();

		raportService.zapiszRaport(bajty);

		sprawdzSchematem(bajty.toByteArray());
		String xml = bajty.toString(StandardCharsets.UTF_8);
		Assertions.assertTrue(xml.contains("pokoi=\"0\""), xml);
		Assertions.assertFalse(xml.contains("niezakwaterowani"), xml);
	}

	// ----- pomocnicze -----

	private void importujPrzyklad() throws IOException {
		try (InputStream plik = getClass().getResourceAsStream("/dane/przyklad.xml")) {
			Assertions.assertNotNull(plik, "Brak pliku /dane/przyklad.xml w zasobach testowych");
			daneService.importuj(plik);
		}
	}

	private static InputStream strumien(String xml) {
		return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
	}

	/** Rzuca wyjątek z opisem, jeśli XML nie spełnia {@code raport.xsd}. */
	private static void sprawdzSchematem(byte[] xml) throws IOException, SAXException {
		Schema schemat = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
				.newSchema(RaportServiceTest.class.getResource("/xsd/raport.xsd"));
		schemat.newValidator().validate(new StreamSource(new ByteArrayInputStream(xml)));
	}
}
