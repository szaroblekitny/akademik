package org.wojtekz.akademik.namedbean;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;
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
	
	@Autowired
	private transient PokojService pokojService;
	
	@Autowired
	private transient PokojBean pokBean;
	
	@Autowired
	private transient StudentService studentService;
	
	@Autowired
	private transient DaneTestowe daneTestowe;

	@Before
	public void setUp() throws Exception {
		daneTestowe.wrzucTrocheDanychDoBazy();
	}

	@After
	public void tearDown() throws Exception {
		pokojService.deleteAll();
		studentService.deleteAll();
	}

	@Test
	public void testPobierzPokoje() {
		List<String> pokStrList = pokBean.pobierzPokoje();
		Assert.assertEquals(3, pokStrList.size());
		Assert.assertEquals("Pokoj [id=3, numerPokoju=103, liczbaMiejsc=4]", pokStrList.get(2));
	}

}
