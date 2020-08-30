package org.wojtekz.akademik.namedbean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
import org.wojtekz.akademik.util.DaneTestowe;

@Ignore

/**
 * Test beana JSF Zakwateruj.
 * 
 * @author Wojciech Zaręba
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class ZakwaterujBeanTest {
	private static Logger logg = LogManager.getLogger();
	
	private transient ZakwaterujBean zakwaterujBean;
	private transient DaneTestowe daneTestowe;
	private transient Messagesy komunikaty;
	private StudentRepository studentRepo;
	private PokojRepository pokojRepo;
	
	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	@Autowired
	public void setPokojRepo(PokojRepository pokojRepo) {
		this.pokojRepo = pokojRepo;
	}
	
	@Autowired
	public void setZakwaterujBean(ZakwaterujBean zakwaterujBean) {
		logg.debug("-------> setZakwaterujBean {}", zakwaterujBean);
		this.zakwaterujBean = zakwaterujBean;
	}
	
	@Autowired
	public void setDaneTestowe(DaneTestowe daneTestowe) {
		this.daneTestowe = daneTestowe;
	}
	
	
	@Before
	public void setUp() throws Exception {
		logg.debug("-------> setUp ZakwaterujBeanTest");
		komunikaty = mock(Messagesy.class);
		zakwaterujBean.setMessagesy(komunikaty);
		daneTestowe.wrzucTrocheDanychDoBazy();
	}

	@After
	public void tearDown() throws Exception {
		pokojRepo.deleteAll();
		studentRepo.deleteAll();
	}

	/**
	 * Test funkcji kwaterowania.
	 * 
	 */
	@Test
	public void testZakwateruj() {
		logg.debug("-------> testZakwateruj");
		assertNotNull(zakwaterujBean);
		zakwaterujBean.zakwateruj();
		// TO DO zakwaterowanie będzie działać inaczej, nie przez dane w tabeli kwaterunek
		
	}

}
