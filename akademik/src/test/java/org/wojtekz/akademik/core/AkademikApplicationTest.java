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
import org.wojtekz.akademik.entity.Pokoj;

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
	private static Path path;
	private static Charset charset = StandardCharsets.UTF_8;
	
	@Autowired
	PokojService pokService;
	
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
		
		// saveObjectList(BufferedWriter writer, List<T> list)
		
		path = FileSystems.getDefault().getPath("pokoje_test_appl.xml");
		try {
			BufferedWriter bufWriter = Files.newBufferedWriter(path, charset);
			logg.debug("----->>> " + pokoje.toString());
			plikowanie.saveObjectList(bufWriter, pokoje);
			bufWriter.close();
		} catch (IOException ee) {
			logg.error("----- ERROR >> B³¹d tworzenia pliku z pokojami");
			Assert.fail("Exception: " + ee.getMessage());
		}
		
		logg.debug("----->>> przedTest mamy plik z pokojami");
		
		// kasujemy pokoje dla czystoœci
		pokService.deleteAll();
		
		
		
	}

	/**
	 * Test, czy aplikacja umie pobraæ dane z pliku i zapisaæ je w bazie danych.
	 */
	@Test
	public void testPobierzPokoje() {
		logg.debug("----->>> testPobierzPokoje starts");
		
		try {
			BufferedReader reader = Files.newBufferedReader(path, charset);
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

}
