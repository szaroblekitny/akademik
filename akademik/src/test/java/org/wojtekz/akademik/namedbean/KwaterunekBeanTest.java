package org.wojtekz.akademik.namedbean;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.services.KwaterunekService;


/**
 * Test beana JSF dla kwaterunku.
 * 
 * @author wojciech Zaręba
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class KwaterunekBeanTest {
private static Logger logg = LogManager.getLogger();
	
	private Kwaterunek kwt1 = new Kwaterunek();
	private Kwaterunek kwt2 = new Kwaterunek();
	
	@Autowired
	private KwaterunekService kwatService;
	
	@Autowired
	private KwaterunekBean kwtBean;
	
	@Before
	public void setUp() throws Exception {
		logg.debug("----->>> setupik dla KwaterunekServiceImplTest");
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

	@Test
	public void testPobierzKwaterunki() {
		List<String> lisKwa = kwtBean.pobierzKwaterunki();
		Assert.assertEquals(2, lisKwa.size());
		Assert.assertEquals("Kwaterunek [id=1, student=2, pokoj=1]", lisKwa.get(0));
	}
	
	@After
	public void setDown() throws Exception {
		kwatService.deleteAll();
	}

}
