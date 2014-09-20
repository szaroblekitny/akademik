package org.wojtekz.akademik.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Student;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class PlikowanieTest {
	private static Logger logg = Logger.getLogger(PlikowanieTest.class.getName());
	
    private Student student;
    private Student drugiStudent;
    private List<Student> listaStudentow = new ArrayList<>();
    private Path path = FileSystems.getDefault().getPath("studenci_test.xml");
    private BufferedWriter buffWriter;
    
    @Autowired
    Plikowanie plikowanie;
    
    @Autowired
    XStreamMarshaller xStreamMarshaller;
    
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
	public void testMarszalowania() {
		Student stt;
		logg.debug("----->>> testMarszalowania");
		plikowanie.setMarshaller(xStreamMarshaller);
		plikowanie.setUnmarshaller(xStreamMarshaller);
		try {
			plikowanie.saveStudenta(student);
			stt = plikowanie.loadStudenta();
			Assert.assertNotNull(stt);
			Assert.assertEquals("Patafian", stt.getNazwisko());
		} catch (IOException ee) {
			logg.error("testMarszalowania: ", ee);
		}
		
	}
	
	@Test
	public void testWriteList() {
		logg.debug("----->>> testWriteList");
		plikowanie.setMarshaller(xStreamMarshaller);
		plikowanie.setUnmarshaller(xStreamMarshaller);
		try {
			buffWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			Plikowanie.saveObjectList(buffWriter, listaStudentow);
			Assert.assertTrue(true);
		} catch (Exception ee) {
			logg.error("testWriteList: ", ee);
			Assert.assertFalse("----->>> Mamy b³¹d zapisu", false);
		}
	}

}
