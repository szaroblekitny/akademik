package org.wojtekz.akademik.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

/**
 * Test danych testowych. Konieczny ze wzgledu na konieczność rozpoznawania
 * typu listy przekazywanej do zapisywania w bazie.
 * 
 * @author Wojtek
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class DaneTestoweTest {
	private static Logger logg = LogManager.getLogger();
	
	@Autowired
	DaneTestowe daneDane;
	
	@After
	public void setDown() throws Exception {
		logg.debug("----->>> Kasowanie danych po teście");
		// TO DO studServ.deleteAll();
		// TO DO pokService.deleteAll();
	}

	@Test
	public void testWrzucTrocheDanychDoBazyListOfT() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazyListOfT");
		daneDane.wrzucTrocheDanychDoBazy(daneDane.getMieszkancy());
		logg.debug("----->>> Dane wrzucone, sprawdzam");
		// TO DO Student stt = studServ.findById(3);
		// TO DO Assert.assertEquals("Malinowski", stt.getNazwisko());
	}

	@Test
	public void testWrzucTrocheDanychDoBazyListOfPokojListOfStudent() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazyListOfPokojListOfStudent start");
		daneDane.wrzucTrocheDanychDoBazy(daneDane.getPokoje(), daneDane.getMieszkancy());
		// TO DO Pokoj pok = pokService.findByNumber("102");
		// TO DO Assert.assertEquals(3, pok.getLiczbaMiejsc());
	}

	
	@Test
	public void testWrzucTrocheDanychDoBazy() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazy start");
		daneDane.wrzucTrocheDanychDoBazy();
		/* TO DO
		long ilePokoi = pokService.ilePokoi();
		Assert.assertEquals("Liczba pokoi niezgodna", 3, ilePokoi);
		long ileStudentow = studServ.iluStudentow();
		Assert.assertEquals("Liczba studnetow niezgodna", 6, ileStudentow);
		*/
	}
}
