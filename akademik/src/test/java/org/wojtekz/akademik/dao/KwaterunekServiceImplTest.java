package org.wojtekz.akademik.dao;

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
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.KwaterunekService;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;
import org.wojtekz.akademik.util.DaneTestowe;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class KwaterunekServiceImplTest {
	private static Logger logg = Logger.getLogger(KwaterunekServiceImplTest.class.getName());
	
	private Kwaterunek kwt1 = new Kwaterunek();
	private Kwaterunek kwt2 = new Kwaterunek();
	
	@Autowired
	private KwaterunekService kwatService;
	
	@Autowired
	private PokojService pokService;
	
	@Autowired
	private StudentService studService;
	
	@Autowired
	private DaneTestowe danne;
	
	@Before
	public void setUp() throws Exception {
		logg.debug("----->>> setupik");
		danne.wrzucTrocheDanychDoBazy();
		logg.debug("----->>> setupik dane wrzucone");
		kwt1.setId(1);
		kwt1.setPokoj(1);
		kwt1.setStudent(2);
		
		kwt2.setId(2);
		kwt2.setPokoj(3);
		kwt2.setStudent(15);
		
		kwatService.save(kwt1);
		kwatService.save(kwt2);
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

	@Test
	public void testFindByIdPokoju() {
		logg.debug("----->>> testFindByIdPokoju");
		// List<Kwaterunek> findByIdPokoju(long idPokoju)
		List<Kwaterunek> lKwa = kwatService.findByIdPokoju(3);
		Assert.assertEquals(15, lKwa.get(0).getStudent());
	}

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
