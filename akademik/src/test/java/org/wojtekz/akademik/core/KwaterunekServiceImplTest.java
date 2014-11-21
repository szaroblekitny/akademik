package org.wojtekz.akademik.core;

// import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Student;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class KwaterunekServiceImplTest {
	private static Logger logg = Logger.getLogger(KwaterunekServiceImplTest.class.getName());
	private DaneTestowe danne = new DaneTestowe();
	private Kwaterunek kwt1 = new Kwaterunek();
	
	@Autowired
	KwaterunekService kwatService;
	
	@Autowired
	PokojService pokService;
	
	@Autowired
	StudentService studService;
	
	@Before
	public void setUp() throws Exception {
		logg.debug("----->>> setupik");
		danne.wrzucTrocheDanychDoBazy();
		logg.debug("----->>> setupik dane wrzucone");
		kwt1.setId(1);
		kwt1.setPokoj(1);
		kwt1.setStudent(2);
		
		kwatService.save(kwt1);
		logg.debug("----->>> setupik koniec setupiku");
	}
	
	@After
	public void setDown() throws Exception {
		kwatService.deleteAll();
		studService.deleteAll();
		pokService.deleteAll();
	}

	@Test
	public void testFindByIdStudenta() {
		logg.debug("----->>> testFindByIdStudenta");
		List<Kwaterunek> kk = kwatService.findByIdStudenta(2);
		Assert.assertEquals(1, kk.get(0).getId());
	}

	/*@Test
	public void testFindByIdPokoju() {
		logg.debug("----->>> testFindByIdPokoju");
		Assert.fail("Not yet implemented");
	}*/

	@Test
	public void testFindStudenciWPokoju() {
		logg.debug("----->>> testFindStudenciWPokoju");
		
		List<Student> stdWPokoju = kwatService.findStudenciWPokoju(1);
		Assert.assertNotNull(stdWPokoju);
		Assert.assertEquals(1, stdWPokoju.size());
		
		logg.info("----->>> Studenci w pokoju 1:");
		for (Student student: stdWPokoju) {
			logg.info("----->>> " + student.toString());
		}
		
		Assert.assertEquals("Kowalska", stdWPokoju.get(0).getNazwisko());
	}

}
