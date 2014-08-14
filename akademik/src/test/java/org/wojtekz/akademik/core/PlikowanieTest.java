package org.wojtekz.akademik.core;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    Plikowanie plikowanie;
    	
    @Before
    public void before() {
    	logg.debug("----->>> Plikowanie before method fired");
    	
    	student = new Student();
    	student.setId(1);
    	student.setImie("Ignacy");
    	student.setNazwisko("Patafian");
    	student.setPlec(Plec.Mezczyzna);
    }
    
	@Test
	public void testMarszalowania() {
		Student stt;
		logg.debug("----->>> testMarszalowania");
		try {
			plikowanie.saveStudenta(student);
			stt = plikowanie.loadStudenta();
			Assert.assertNotNull(stt);
			Assert.assertEquals("Patafian", stt.getNazwisko());
		} catch (IOException ee) {
			logg.error("testMarszalowania: ", ee);
		}
		
		
		
	}

}
