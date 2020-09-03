package org.wojtekz.akademik.namedbean;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Test beana JSF dla pokoi. Generalnie chodzi o to, że Sonar
 * bardzo sugeruje testować też takie klasy.
 * 
 * @author Wojciech Zaręba
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class PokojBeanTest {
	private transient PokojBean pokBean;
	private PokojRepository pokojRepository;
	private StudentRepository studentRepo;
	private DaneTestowe daneTestowe;
	
	@Autowired
	public void setPokBean(PokojBean pokBean) {
		this.pokBean = pokBean;
	}

	@Autowired
	public void setPokojRepository(PokojRepository pokojRepository) {
		this.pokojRepository = pokojRepository;
	}
	
	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	//  TO DO !!! -- tu jest drugie wywołanie bazy -- !!!
	
	
	@Autowired
	public void setDaneTestowe(DaneTestowe daneTestowe) {
		this.daneTestowe = daneTestowe;
	}

	@Before
	public void setUp() throws Exception {
		daneTestowe.wrzucTrocheDanychDoBazy();
	}

	@After
	public void tearDown() throws Exception {
		pokojRepository.deleteAll();
		studentRepo.deleteAll();
	}

	@Ignore
	@Test
	public void testPobierzPokoje() {
		Assert.assertEquals(3, pokojRepository.count());
		List<String> pokStrList = pokBean.pobierzPokoje();
		Assert.assertEquals(3, pokStrList.size());
		Assert.assertEquals("Pokoj [id=3, numerPokoju=103, liczbaMiejsc=4]", pokStrList.get(2));
	}

}
