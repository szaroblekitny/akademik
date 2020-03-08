package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class PlikowanieTest {
	private static Logger logg = LogManager.getLogger();
	
    private Student student;
    private Student drugiStudent;
    private List<Student> listaStudentow = new ArrayList<>();
    private Path path = FileSystems.getDefault().getPath("studenci_test.xml");
    private BufferedWriter buffWriter;
    
    @Autowired
    Plikowanie plikowanie;
    
    @Before
    public void before() {
    	logg.debug("----->>> Plikowanie test before method fired");
    	
    	student = new Student();
    	student.setId(1);
    	student.setImie("Ignacy");
    	student.setNazwisko("Patafian");
    	student.setPlec(Plec.Mezczyzna);
    	
    	drugiStudent = new Student();
    	drugiStudent.setId(2);
    	drugiStudent.setImie("Jan");
    	drugiStudent.setNazwisko("Kowalski");
    	drugiStudent.setPlec(Plec.Mezczyzna);
    	
    	listaStudentow.add(student);
    	listaStudentow.add(drugiStudent);
    	
    }
    
	
	@Test
	public void testWriteListyStudentow() {
		logg.debug("----->>> testWriteList");
		try {
			buffWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			plikowanie.saveObjectList(buffWriter, listaStudentow);
			Assert.assertTrue(true);
		} catch (Exception ee) {
			logg.error("----- ERROR >> testWriteList: ", ee);
			Assert.assertFalse("----->>> Mamy błąd zapisu", true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMarszaluListy() {
		Pokoj pok1 = new Pokoj();
		pok1.setId(1);
		pok1.setLiczbaMiejsc(3);
		pok1.setNumerPokoju("101");
		Pokoj pok2 = new Pokoj();
		pok2.setId(2);
		pok2.setLiczbaMiejsc(3);
		pok2.setNumerPokoju("102");
		
		List<Pokoj> pokoje = new ArrayList<Pokoj>();
		pokoje.add(pok1);
		pokoje.add(pok2);
		
		List<Pokoj> listaPokoi = new ArrayList<Pokoj>();
		
		Path plik = FileSystems.getDefault().getPath("pokoje_test.xml");
		try {
			BufferedWriter buWri = Files.newBufferedWriter(plik, StandardCharsets.UTF_8);
			BufferedReader buReader = Files.newBufferedReader(plik, StandardCharsets.UTF_8);
			plikowanie.saveObjectList(buWri, pokoje);
			listaPokoi = (List<Pokoj>)plikowanie.loadObjectList(buReader);
		} catch (Exception ex) {
			logg.error("----- ERROR >> testWriteList: ", ex);
			Assert.assertFalse("----->>> Mamy błąd zapisu", true);
		}
		
		logg.debug("----->>> Odczytana lista pokoi:");
		for (Pokoj pp: listaPokoi) {
			logg.debug("----->>> " + pp.toString());
		}
		
		Assert.assertEquals("102", listaPokoi.get(1).getNumerPokoju());
	}

}
