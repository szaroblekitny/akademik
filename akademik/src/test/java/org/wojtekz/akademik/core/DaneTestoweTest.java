package org.wojtekz.akademik.core;


import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Student;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class DaneTestoweTest {
	private static Logger logg = Logger.getLogger(DaneTestoweTest.class.getName());
	DaneTestowe daneDane = new DaneTestowe();
	
	@Autowired
	StudentService studServ;

//	@Before
//	public void setUp() throws Exception {
//	}
	
	@After
	public void setDown() {
		studServ.deleteAll();
	}

	@Test
	public void testWrzucTrocheDanychDoBazyListOfT() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazyListOfT");
		daneDane.wrzucTrocheDanychDoBazy(daneDane.getMieszkancy());
		logg.debug("----->>> Dane wrzucone, ale czy na pewno?");
		Student stt = studServ.findById(3);
		Assert.assertEquals("Malinowski", stt.getNazwisko());
	}

	/*@Test
	public void testWrzucTrocheDanychDoBazyListOfPokojListOfStudent() {
		Assert.fail("Not yet implemented");
	}*/

}
