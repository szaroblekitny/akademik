package org.wojtekz.akademik.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Główny test głównej klasy naszego Akademika.
 * 
 * @author Wojtek
 *
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
public class AkademikTest {
	private final String PLIK_DANYCH = "akademik_test_appl.xml";
	private final String PLIK_RAPORTU = "raport_test_appl.txt";
	
	private static Logger logg = LogManager.getLogger();
	private static Path pathDanych;
	private static Path pathRaportu;
	private static Charset charset = StandardCharsets.UTF_8;
	
	private List<Pokoj> pokoje = new ArrayList<Pokoj>();
	private List<Student> studenci = new ArrayList<Student>();

	private Akademik akademik;
	private DaneTestowe daneTestowe;
	private Plikowanie plikowanie;
	private StudentRepository studentRepo;
	private PokojRepository pokojRepo;
	
	@Autowired
	public void setAkademik(Akademik akademik) {
		this.akademik = akademik;
	}

	@Autowired
	public void setDaneTestowe(DaneTestowe daneTestowe) {
		this.daneTestowe = daneTestowe;
	}

	@Autowired
	public void setPlikowanie(Plikowanie plikowanie) {
		this.plikowanie = plikowanie;
	}
	
	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	@Autowired
	public void setPokojRepo(PokojRepository pokojRepo) {
		this.pokojRepo = pokojRepo;
	}

	@BeforeEach
	public void przedTest() throws Exception {
		logg.debug("-----+> przedTest starts");
		
		if (!pokoje.isEmpty()) {
			for(Pokoj pok : pokoje) {
				pokoje.remove(pok);
			}
		}
		
		if (!studenci.isEmpty()) {
			for(Student stud : studenci) {
				studenci.remove(stud);
			}
		}
		
		// Pokoje
		Pokoj pok1 = new Pokoj();
		pok1.setId(1);
		pok1.setLiczbaMiejsc(2);
		pok1.setNumerPokoju("101");
		Pokoj pok2 = new Pokoj();
		pok2.setId(2);
		pok2.setLiczbaMiejsc(3);
		pok2.setNumerPokoju("102");
		
		pokoje.add(pok1);
		pokoje.add(pok2);
		
		// Studenci:
		Student kowalski = new Student();
		kowalski.setId(1);
		kowalski.setImie("Jan");
		kowalski.setNazwisko("Kowalski");
		kowalski.setPlec(Plec.MEZCZYZNA);
		
		Student kowalska = new Student();
		kowalska.setId(2);
		kowalska.setImie("Janina");
		kowalska.setNazwisko("Kowalska");
		kowalska.setPlec(Plec.KOBIETA);
		
		Student malinowski = new Student();
		malinowski.setId(3);
		malinowski.setImie("Adam");
		malinowski.setNazwisko("Malinowski");
		malinowski.setPlec(Plec.MEZCZYZNA);
		
		Student nowak = new Student();
		nowak.setId(4);
		nowak.setImie("Mirosław");
		nowak.setNazwisko("Nowak");
		nowak.setPlec(Plec.MEZCZYZNA);
		
		Student malinowski2 = new Student();
		malinowski2.setId(5);
		malinowski2.setImie("Jerzy");
		malinowski2.setNazwisko("Malinowski");
		malinowski2.setPlec(Plec.MEZCZYZNA);
		
		// Plik nie ma ID, więc import nadaje je w kolejności z pliku (krok 2.1a).
		// Ta kolejność daje te same ID, które studenci mieli wcześniej w pliku XStreama.
		studenci.add(kowalski);
		studenci.add(kowalska);
		studenci.add(malinowski);
		studenci.add(nowak);
		studenci.add(malinowski2);
		
		
		logg.trace("-----> Przygotowanie plików");
		pathDanych = FileSystems.getDefault().getPath(PLIK_DANYCH);
		pathRaportu = FileSystems.getDefault().getPath(PLIK_RAPORTU);
		logg.debug("-----> Pokoje: {}", pokoje);
		logg.debug("-----> Studenci: {}", studenci);
		try (OutputStream plik = Files.newOutputStream(pathDanych)) {
			plikowanie.zapiszDane(plik, pokoje, studenci);
		}
		
		// usuwa dane o studentch i pokojach z bazy
		logg.trace("-----> przed usunDane");
		usunDane();
		logg.debug("-------+> @Before przedTest koniec");
		
	}

	// testPobierzPokoje i testPobierzStudentow przeniesione do DaneServiceTest
	// (krok 2.1 planu naprawczego: import przeszedł z Akademik do DaneService)

	/**
	 * Test metody kwaterowania akademika. Używa danych z konstruktora,
	 * a nie z klasy {@link org.wojtekz.akademik.util.DaneTestowe}.
	 * 
	 * Testuje zapis pokoi i studentów do bazy, wykonuje metodę zakwateruj(),
	 * sprawdza spójność danych i czy kwaterowanie jest udane: nie powinno być,
	 * bo mamy za dużo mężczyzn - kobieta zajmuje 1 pokój, a mężczyzn jest czterech.
	 */
	@Test
	public void testKwaterowania() {
		logg.debug("=============>>> testKwaterowania starts");
		pokojRepo.saveAll(pokoje);
		Assertions.assertEquals(2, pokojRepo.findAll().size());
		
		studentRepo.saveAll(studenci);
		Assertions.assertEquals(5, studentRepo.findAll().size());
		
		logg.trace("-----> pokoje i studenci w bazie");
		String komunikat = akademik.zakwateruj();
		Assertions.assertNotEquals(studentRepo.findAll(), studenci);
		Assertions.assertEquals("Brakuje pokoi dla mężczyzn", komunikat, "Kwaterowanie nie powinno się udać");
		
		List<Pokoj> wszystkiePokBaza = pokojRepo.findAll();
		long ileBaza = pokojRepo.count();
		long ilePoko = pokoje.size();
		Assertions.assertEquals(ilePoko, ileBaza);
		
		Assertions.assertEquals(pokoje.get(0).getNumerPokoju(), wszystkiePokBaza.get(0).getNumerPokoju());
		Assertions.assertEquals(pokoje.get(1).getNumerPokoju(), wszystkiePokBaza.get(1).getNumerPokoju());
	}
	
	/**
	 * Test opróżniania akademika. Sprawdza, czy numery pokoi dla studentów są puste.
	 */
	@Test
	public void testOprozniania() {
		logg.debug("=============>>> testOprozniania starts");
		daneTestowe.wrzucTrocheDanychDoBazy();
		logg.trace("-----> przed zakwateruj");
		akademik.zakwateruj();
		logg.trace("-----> po zakwateruj");

		akademik.oproznij();
		for (Student student : studentRepo.findAll()) {
			Assertions.assertNull(student.getPokoj());
		}
	}

	/**
	 * Test głównej aplikacji plikowej.
	 */
	@Test
	public void testAkademika() {
		logg.debug("=============>>> testAkademika starts");
		try (
			InputStream dane = Files.newInputStream(pathDanych);
			BufferedWriter outputWriter = Files.newBufferedWriter(pathRaportu, charset))
		{
			akademik.akademik(dane, outputWriter);
			
			List<String> wszyskieLinie = Files.readAllLines(pathRaportu, charset);
			if (logg.isTraceEnabled()) {
				logg.trace("wszyskieLinie =====>>> {}", Arrays.toString(wszyskieLinie.toArray()));
			}
			
			int ileLinii = wszyskieLinie.size();
			Assertions.assertEquals("Pokoj [id=1, numerPokoju=101, liczbaMiejsc=2]", wszyskieLinie.get(0));
			Assertions.assertEquals("Student [id=3, imie=Adam, nazwisko=Malinowski, plec=MEZCZYZNA, nr pokoju=102]", wszyskieLinie.get(ileLinii - 5));
			
		} catch (IOException ie) {
			logg.error("----->>> testAkademika - problemy plikowe", ie);
			Assertions.fail("Exception: " + ie.getMessage());
		}
	}
	
	private void wyswietlMieszkancow(List<Student> mieszki) {
		if (mieszki == null) {
			throw new NullPointerException("------!!> Pusta lista mieszkańców");
		}
		
		if (mieszki.isEmpty()) {
			throw new RuntimeException("------!!> Mieszkańcy muszą być");
		}
		
		for (Student mieszka : mieszki) {
			logg.trace("--+> {}", mieszka);
		}
	}
	
	/**
	 * Test, czy zakwaterowani studenci nie są różnej płci.
	 */
	@Test
	public void testPlciowy() {
		logg.debug("=============>>> testPlciowy starts");
		daneTestowe.wrzucTrocheDanychDoBazy();
		logg.trace("-----> przed zakwateruj testPlciowy");
		Optional<Pokoj> testowanyPokoj = pokojRepo.findById(1L);
		Assertions.assertTrue(testowanyPokoj.isPresent());
		akademik.zakwateruj();
		List<Student> mieszkancy = studentRepo.findByPokoj(testowanyPokoj.get());
		Assertions.assertEquals(2, mieszkancy.size());
		//----
		if (logg.isTraceEnabled()) {
			logg.trace("--------+> mieszkańcy pok. 101");
			wyswietlMieszkancow(mieszkancy);
		}
		//----
		Assertions.assertEquals("Janina", mieszkancy.get(0).getImie(), "Oczekuję Janiny, a nie mam");
		Assertions.assertEquals("Małgorzata", mieszkancy.get(1).getImie(), "Oczekuję Małgorzaty, a nie mam");
		logg.trace("--------+> drugi pokój");
		// -- drugi pokój
		testowanyPokoj = pokojRepo.findById(2L);
		logg.trace("--------+>mamy drugi pokój: {}", testowanyPokoj);
		Assertions.assertTrue(testowanyPokoj.isPresent());
		mieszkancy = studentRepo.findByPokoj(testowanyPokoj.get());
		Assertions.assertEquals(3, mieszkancy.size());
		if (logg.isTraceEnabled()) {
			logg.trace("--------+> mieszkańcy pok. 102");
			wyswietlMieszkancow(mieszkancy);
		}
		// Studenci są ładowani z bazy testowe wg identyfikatorów z pola ID
		Assertions.assertEquals("Jan", mieszkancy.get(0).getImie());
		Assertions.assertEquals("Adam", mieszkancy.get(1).getImie());
		Assertions.assertEquals("Mirosław", mieszkancy.get(2).getImie());
		// -- trzeci pokój
		testowanyPokoj = pokojRepo.findById(3L);
		Assertions.assertTrue(testowanyPokoj.isPresent());
		mieszkancy = studentRepo.findByPokoj(testowanyPokoj.get());
		Assertions.assertEquals(1, mieszkancy.size());
		if (logg.isTraceEnabled()) {
			logg.trace("--------+> mieszkańcy pok. 103");
			wyswietlMieszkancow(mieszkancy);
		}
		
		Assertions.assertEquals("Ignacy", mieszkancy.get(0).getImie(), "Oczekuję Ignacego w pok. 3");
	}
	
	
	@Test
	public void testStanuAkademika() {
		logg.debug("=============>>> testStanuAkademika starts");
		
		daneTestowe.wrzucTrocheDanychDoBazy();
		logg.trace("-----> przed zakwateruj");
		akademik.zakwateruj();
		logg.trace("-----> po zakwateruj");
		
		try {
			BufferedWriter outputWriter = Files.newBufferedWriter(pathRaportu, charset);
			akademik.podajStanAkademika(outputWriter, "testStanuAkademika");
			outputWriter.close();
			List<String> wszyskieLinie = Files.readAllLines(pathRaportu, charset);
			int ileLinii = wszyskieLinie.size();
			Assertions.assertEquals("===================", wszyskieLinie.get(ileLinii - 1));
		} catch (IOException ee) {
			logg.error("-----> testStanuAkademika Błąd bufora ", ee);
			Assertions.fail("Exception: " + ee.getMessage());
		}
		
	}
	
	@AfterEach
	public void poTescie() throws Exception {
		logg.debug("== == == == ==>>> poTescie");
		usunPlik(pathDanych);
		
		// jeśli jest, usuwa plik raportu testowego
		if (Files.exists(pathRaportu)) {
			usunPlik(pathRaportu);
		}
		
		logg.debug("-------------------+> usunięte pliki po tescie");
	}
	
	/**
	 * usuwa dane z bazy
	 */
	private void usunDane() {
		studentRepo.deleteAll();
		logg.trace("-----+> studenci usunięci");
		pokojRepo.deleteAll();
		logg.trace("-----+> pokoje usunięte");
		
	}
	
	private void usunPlik(Path plik) throws IOException {
		logg.trace("-----+> kasowanie pliku " + plik.getFileName());
		Files.delete(plik);
	}

}
