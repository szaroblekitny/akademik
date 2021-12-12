package org.wojtekz.akademik.core;

import org.junit.After;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class AkademikTest {
	private final String PLIK_POKOI = "pokoje_test_appl.xml";
	private final String PLIK_STUDENTOW = "studenci_test_appl.xml";
	private final String PLIK_RAPORTU = "raport_test_appl.txt";
	
	private static Logger logg = LogManager.getLogger();
	private static Path pathPokoi;
	private static Path pathStudentow;
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

	@Before
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
		
		studenci.add(kowalska);
		studenci.add(kowalski);
		studenci.add(nowak);
		studenci.add(malinowski);
		studenci.add(malinowski2);
		
		
		logg.trace("-----> Przygotowanie plików");
		pathPokoi = FileSystems.getDefault().getPath(PLIK_POKOI);
		pathStudentow = FileSystems.getDefault().getPath(PLIK_STUDENTOW);
		pathRaportu = FileSystems.getDefault().getPath(PLIK_RAPORTU);
		BufferedWriter bufWriter = Files.newBufferedWriter(pathPokoi, charset);
		logg.debug("-----> Pokoje: {}", pokoje);
		if (bufWriter == null) {
			throw new IOException("----->>> bufWriter jest nullem");
		}
		plikowanie.saveObjectList(bufWriter, pokoje);
		bufWriter.close();
			
		bufWriter = Files.newBufferedWriter(pathStudentow, charset);
		logg.debug("-----> Studenci: {}", studenci);
		plikowanie.saveObjectList(bufWriter, studenci);
		bufWriter.close();
		
		// usuwa dane o studentch i pokojach z bazy
		logg.trace("-----> przed usunDane");
		usunDane();
		logg.debug("-------+> @Before przedTest koniec");
		
	}

	/**
	 * Test, czy aplikacja umie pobrać dane z pliku i zapisać je w bazie danych.
	 */
	@Test
	public void testPobierzPokoje() {
		logg.debug("=============>>> testPobierzPokoje starts");
		
		try {
			BufferedReader reader = Files.newBufferedReader(pathPokoi, charset);
			akademik.pobierzZPliku(reader);
			reader.close();
			
			List<Pokoj> pokZBazy = pokojRepo.findByNumerPokoju("102");
			Assert.assertEquals(3, pokZBazy.get(0).getLiczbaMiejsc());
			
		} catch (IOException ee) {
			logg.error("----->>> testPobierzPokoje: Błąd odczytu pliku z pokojami", ee);
			Assert.fail("Exception: " + ee.getMessage());
		}
		
	}
	
	@Test
	public void testPobierzStudentow () {
		logg.debug("=============>>> testPobierzStudentow starts");
		
		try {
			BufferedReader reader = Files.newBufferedReader(pathStudentow, charset);
			akademik.pobierzZPliku(reader);
			reader.close();
			logg.trace("-----> studenci pobrani");
			
			List<Student> malinowscyZBazy = studentRepo.findByName("Malinowski");
			Assert.assertEquals(3, malinowscyZBazy.get(0).getId());
			Assert.assertEquals("Adam", malinowscyZBazy.get(0).getImie());
			Assert.assertEquals("Jerzy", malinowscyZBazy.get(1).getImie());
			
		} catch (IOException ee) {
			logg.error("----- ERROR >> Błąd odczytu pliku ze studentami");
			Assert.fail("Exception: " + ee.getMessage());
		}
		
	}
	
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
		Assert.assertEquals(2, pokojRepo.findAll().size());
		
		studentRepo.saveAll(studenci);
		Assert.assertEquals(5, studentRepo.findAll().size());
		
		logg.trace("-----> pokoje i studenci w bazie");
		boolean udane = akademik.zakwateruj();
		Assert.assertNotEquals(studentRepo.findAll(), studenci);
		Assert.assertFalse("Kwaterowanie nie powinno się udać", udane);
		
		List<Pokoj> wszystkiePokBaza = pokojRepo.findAll();
		long ileBaza = pokojRepo.count();
		long ilePoko = pokoje.size();
		Assert.assertEquals(ilePoko, ileBaza);
		
		Assert.assertEquals(pokoje.get(0).getNumerPokoju(), wszystkiePokBaza.get(0).getNumerPokoju());
		Assert.assertEquals(pokoje.get(1).getNumerPokoju(), wszystkiePokBaza.get(1).getNumerPokoju());
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
			Assert.assertNull(student.getPokoj());
		}
	}

	/**
	 * Test głównej aplikacji plikowej.
	 */
	@Test
	public void testAkademika() {
		logg.debug("=============>>> testAkademika starts");
		try (
			BufferedReader pokojeReader = Files.newBufferedReader(pathPokoi, charset);
			BufferedReader studenciReader = Files.newBufferedReader(pathStudentow, charset);
			BufferedWriter outputWriter = Files.newBufferedWriter(pathRaportu, charset))
		{
			akademik.akademik(pokojeReader, studenciReader, outputWriter);
			
			List<String> wszyskieLinie = Files.readAllLines(pathRaportu, charset);
			if (logg.isTraceEnabled()) {
				logg.trace("wszyskieLinie =====>>> {}", Arrays.toString(wszyskieLinie.toArray()));
			}
			
			int ileLinii = wszyskieLinie.size();
			Assert.assertEquals("Pokoj [id=1, numerPokoju=101, liczbaMiejsc=2]", wszyskieLinie.get(0));
			Assert.assertEquals("Student [id=3, imie=Adam, nazwisko=Malinowski, plec=MEZCZYZNA, nr pokoju=102]", wszyskieLinie.get(ileLinii - 5));
			
		} catch (IOException ie) {
			logg.error("----->>> testAkademika - problemy plikowe", ie);
			Assert.fail("Exception: " + ie.getMessage());
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
		Assert.assertTrue(testowanyPokoj.isPresent());
		akademik.zakwateruj();
		List<Student> mieszkancy = studentRepo.findByPokoj(testowanyPokoj.get());
		Assert.assertEquals(2, mieszkancy.size());
		//----
		if (logg.isTraceEnabled()) {
			logg.trace("--------+> mieszkańcy pok. 101");
			wyswietlMieszkancow(mieszkancy);
		}
		//----
		Assert.assertEquals("Oczekuję Janiny, a nie mam", "Janina", mieszkancy.get(0).getImie());
		Assert.assertEquals("Oczekuję Małgorzaty, a nie mam", "Małgorzata", mieszkancy.get(1).getImie());
		logg.trace("--------+> drugi pokój");
		// -- drugi pokój
		testowanyPokoj = pokojRepo.findById(2L);
		logg.trace("--------+>mamy drugi pokój: {}", testowanyPokoj);
		Assert.assertTrue(testowanyPokoj.isPresent());
		mieszkancy = studentRepo.findByPokoj(testowanyPokoj.get());
		Assert.assertEquals(3, mieszkancy.size());
		if (logg.isTraceEnabled()) {
			logg.trace("--------+> mieszkańcy pok. 102");
			wyswietlMieszkancow(mieszkancy);
		}
		// Studenci są ładowani z bazy testowe wg identyfikatorów z pola ID
		Assert.assertEquals("Jan", mieszkancy.get(0).getImie());
		Assert.assertEquals("Adam", mieszkancy.get(1).getImie());
		Assert.assertEquals("Mirosław", mieszkancy.get(2).getImie());
		// -- trzeci pokój
		testowanyPokoj = pokojRepo.findById(3L);
		Assert.assertTrue(testowanyPokoj.isPresent());
		mieszkancy = studentRepo.findByPokoj(testowanyPokoj.get());
		Assert.assertEquals(1, mieszkancy.size());
		if (logg.isTraceEnabled()) {
			logg.trace("--------+> mieszkańcy pok. 103");
			wyswietlMieszkancow(mieszkancy);
		}
		
		Assert.assertEquals("Oczekuję Ignacego w pok. 3", "Ignacy", mieszkancy.get(0).getImie());
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
			akademik.podajStanAkademika(outputWriter, true);
			outputWriter.close();
			List<String> wszyskieLinie = Files.readAllLines(pathRaportu, charset);
			int ileLinii = wszyskieLinie.size();
			Assert.assertEquals("===================", wszyskieLinie.get(ileLinii - 1));
		} catch (IOException ee) {
			logg.error("-----> testStanuAkademika Błąd bufora ", ee);
			Assert.fail("Exception: " + ee.getMessage());
		}
		
	}
	
	@After
	public void poTescie() throws Exception {
		logg.debug("== == == == ==>>> poTescie");
		usunPlik(pathPokoi);
		usunPlik(pathStudentow);
		
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
