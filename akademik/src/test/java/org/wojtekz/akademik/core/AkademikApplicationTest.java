package org.wojtekz.akademik.core;

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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

/**
 * G³ówny test g³ównej klasy naszego Akademika.
 * 
 * @author Wojtek
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class AkademikApplicationTest {
	private static Logger logg = Logger.getLogger(AkademikApplicationTest.class.getName());
	private static Path pathPokoi;
	private static Path pathStudentow;
	private static Charset charset = StandardCharsets.UTF_8;
	
	@Autowired
	PokojService pokService;
	
	@Autowired
	StudentService studService;
	
	@Autowired
	Plikowanie plikowanie;
	
	@Autowired
	AkademikApplication akademik;
	
	@Before
	public void przedTest() {
		logg.debug("----->>> przedTest starts");
		// Tworzymy plik
		Pokoj pok1 = new Pokoj();
		pok1.setId(1);
		pok1.setLiczbaMiejsc(2);
		pok1.setNumerPokoju("101");
		Pokoj pok2 = new Pokoj();
		pok2.setId(2);
		pok2.setLiczbaMiejsc(3);
		pok2.setNumerPokoju("102");
		
		List<Pokoj> pokoje = new ArrayList<Pokoj>();
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
		nowak.setImie("Miros³aw");
		nowak.setNazwisko("Nowak");
		nowak.setPlec(Plec.Mezczyzna);
		
		List<Student> studenci = new ArrayList<Student>();
		studenci.add(kowalska);
		studenci.add(kowalski);
		studenci.add(nowak);
		studenci.add(malinowski);
		
		
		// saveObjectList(BufferedWriter writer, List<T> list)
		
		pathPokoi = FileSystems.getDefault().getPath("pokoje_test_appl.xml");
		pathStudentow = FileSystems.getDefault().getPath("studenci_test_appl.xml");
		try {
			BufferedWriter bufWriter = Files.newBufferedWriter(pathPokoi, charset);
			logg.debug("----->>> " + pokoje.toString());
			plikowanie.saveObjectList(bufWriter, pokoje);
			bufWriter.close();
			
			bufWriter = Files.newBufferedWriter(pathStudentow, charset);
			logg.debug("----->>> " + studenci.toString());
			plikowanie.saveObjectList(bufWriter, studenci);
			bufWriter.close();
		} catch (IOException ee) {
			logg.error("----- ERROR >> B³¹d tworzenia plików");
			Assert.fail("Exception: " + ee.getMessage());
		}
		
		logg.debug("----->>> przedTest mamy pliki z danymi");
		
		// kasujemy dla czystoœci
		pokService.deleteAll();
		studService.deleteAll();
		logg.debug("----->>> przedTest koniec");
		
	}

	/**
	 * Test, czy aplikacja umie pobraæ dane z pliku i zapisaæ je w bazie danych.
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
			logg.error("----- ERROR >> B³¹d odczytu pliku z pokojami");
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
			logg.error("----- ERROR >> B³¹d odczytu pliku ze studentami");
			Assert.fail("Exception: " + ee.getMessage());
		}
		
		logg.debug("----->>> testPobierzStudentow finish");
	}

}
