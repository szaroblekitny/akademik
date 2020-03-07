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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.KwaterunekService;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Główny test głównej klasy naszego Akademika.
 * 
 * @author Wojtek
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class AkademikApplicationTest {
	private final String PLIK_POKOI = "pokoje_test_appl.xml";
	private final String PLIK_STUDENTOW = "studenci_test_appl.xml";
	private static Logger logg = LogManager.getLogger();
	private static Path pathPokoi;
	private static Path pathStudentow;
	private static Charset charset = StandardCharsets.UTF_8;
	
	private List<Pokoj> pokoje = new ArrayList<Pokoj>();
	private List<Student> studenci = new ArrayList<Student>();
	
	@Autowired
	PokojService pokService;
	
	@Autowired
	StudentService studService;
	
	@Autowired
	KwaterunekService kwaterunekService;
	
	@Autowired
	Plikowanie plikowanie;
	
	@Autowired
	AkademikApplication akademik;
	
	@Autowired
	DaneTestowe daneTestowe;
	
	@Before
	public void przedTest() throws Exception {
		logg.debug("----->>> przedTest starts");
		
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
		kowalski.setPlec(Plec.Mezczyzna);
		
		Student kowalska = new Student();
		kowalska.setId(2);
		kowalska.setImie("Janina");
		kowalska.setNazwisko("Kowalska");
		kowalska.setPlec(Plec.Kobieta);
		
		Student malinowski = new Student();
		malinowski.setId(3);
		malinowski.setImie("Adam");
		malinowski.setNazwisko("Malinowski");
		malinowski.setPlec(Plec.Mezczyzna);
		
		Student nowak = new Student();
		nowak.setId(4);
		nowak.setImie("Mirosław");
		nowak.setNazwisko("Nowak");
		nowak.setPlec(Plec.Mezczyzna);
		
		studenci.add(kowalska);
		studenci.add(kowalski);
		studenci.add(nowak);
		studenci.add(malinowski);
		
		
		// saveObjectList(BufferedWriter writer, List<T> list)
		
		pathPokoi = FileSystems.getDefault().getPath(PLIK_POKOI);
		pathStudentow = FileSystems.getDefault().getPath(PLIK_STUDENTOW);
		BufferedWriter bufWriter = Files.newBufferedWriter(pathPokoi, charset);
		logg.debug("----->>> " + pokoje.toString());
		plikowanie.saveObjectList(bufWriter, pokoje);
		bufWriter.close();
			
		bufWriter = Files.newBufferedWriter(pathStudentow, charset);
		logg.debug("----->>> " + studenci.toString());
		plikowanie.saveObjectList(bufWriter, studenci);
		bufWriter.close();
		
		usunDane();
		logg.debug("----->>> przedTest koniec");
		
	}

	/**
	 * Test, czy aplikacja umie pobrać dane z pliku i zapisać je w bazie danych.
	 */
	@Test
	public void testPobierzPokoje() {
		logg.debug("----->>> testPobierzPokoje starts");
		
		try {
			BufferedReader reader = Files.newBufferedReader(pathPokoi, charset);
			akademik.pobierzPokoje(reader);
			reader.close();
			
			Pokoj pokZBazy = pokService.findByNumber("102");
			Assert.assertEquals(3, pokZBazy.getLiczbaMiejsc());
			
		} catch (IOException ee) {
			logg.error("----- ERROR >> Błąd odczytu pliku z pokojami");
			Assert.fail("Exception: " + ee.getMessage());
		}
		
		logg.debug("----->>> testPobierzPokoje finish");
	}
	
	@Test
	public void testPobierzStudentow () {
		logg.debug("----->>> testPobierzStudentow starts");
		
		try {
			BufferedReader reader = Files.newBufferedReader(pathStudentow, charset);
			akademik.pobierzStudentow(reader);
			reader.close();
			
			Student malinowskiZBazy = studService.findByName("Malinowski");
			Assert.assertEquals(3, malinowskiZBazy.getId());
			
		} catch (IOException ee) {
			logg.error("----- ERROR >> Błąd odczytu pliku ze studentami");
			Assert.fail("Exception: " + ee.getMessage());
		}
		
		logg.debug("----->>> testPobierzStudentow finish");
	}
	
	
	@Test
	public void testKwaterunku() {
		logg.debug("----->>> testKwaterunku starts");
		zapelnijDanymi();
		Assert.assertEquals(2, pokService.ilePokoi());
		Assert.assertEquals(4, studService.iluStudentow());
		
		akademik.zakwateruj();
		List<Kwaterunek> powiazania = kwaterunekService.listAll();
		Assert.assertEquals(4, powiazania.size());
		
		usunDane();
	}
	
	
	@Test
	public void testStanuAkademika() {
		logg.debug("----->>> testStanuAkademika starts");
		
		daneTestowe.wrzucTrocheDanychDoBazy();
		akademik.zakwateruj();
		
		try {
			BufferedWriter outputWriter = Files.newBufferedWriter(FileSystems.getDefault()
					.getPath("stan_akademika.txt"), charset);
			akademik.podajStanAkademika(outputWriter, true);
			outputWriter.close();
			List<String> wszyskieLinie = Files.readAllLines(FileSystems.getDefault()
					.getPath("stan_akademika.txt"), charset);
			int ileLinii = wszyskieLinie.size();
			Assert.assertEquals("===================", wszyskieLinie.get(ileLinii - 1));
		} catch (IOException ee) {
			logg.error("----->>> testStanuAkademika Błąd bufora ", ee);
			Assert.fail("Exception: " + ee.getMessage());
		} finally {
			usunDane();
		}
		
	}
	
	@After
	public void poTescie() throws Exception {
		usunPlik(pathPokoi);
		usunPlik(pathStudentow);
	}
	
	/**
	 * wstawia pokoje i studentów do bazy
	 */
	private void zapelnijDanymi() {
		logg.debug("----->>> zapelnijDanymi starts");
		for (Pokoj pokoj : pokoje) {
			pokService.save(pokoj);
		}
		for (Student student : studenci) {
			studService.save(student);
		}
	}
	
	/**
	 * usuwa dane z bazy
	 */
	private void usunDane() {
		logg.debug("----->>> usunDane starts");
		pokService.deleteAll();
		studService.deleteAll();
		kwaterunekService.deleteAll();
	}
	
	private void usunPlik(Path plik) throws IOException {
		logg.debug("----->>> kasowanie pliku " + plik.getFileName());
		Files.delete(plik);
	}

}
