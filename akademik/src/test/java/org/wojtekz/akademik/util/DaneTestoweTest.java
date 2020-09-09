package org.wojtekz.akademik.util;

import java.util.List;
import java.util.Optional;

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
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

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
	
	private DaneTestowe daneDane;
	private StudentRepository studentRepo;
	private PokojRepository pokojRepo;
	
	@Autowired
	public void setDaneDane(DaneTestowe daneDane) {
		this.daneDane = daneDane;
	}

	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	@Autowired
	public void setPokojRepo(PokojRepository pokojRepo) {
		this.pokojRepo = pokojRepo;
	}

	@After
	public void setDown() throws Exception {
		logg.debug("------+> Kasowanie danych po teście");
		studentRepo.deleteAll();
		logg.trace("------+> Studenci skasowani");
		pokojRepo.deleteAll();
		logg.trace("------+> Pokoje skasowane");
	}

	@Test
	public void testWrzucTrocheDanychDoBazyListOfT() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazyListOfT");
		daneDane.wrzucTrocheDanychDoBazy(daneDane.getMieszkancy());
		logg.trace("----->>> Dane wrzucone, sprawdzam");
		Optional<Student> stt = studentRepo.findById(3L);
		Assert.assertFalse(stt.isEmpty());
		Assert.assertEquals("Malinowski", stt.get().getNazwisko());
	}

	@Test
	public void testWrzucTrocheDanychDoBazyListOfPokojListOfStudent() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazyListOfPokojListOfStudent start");
		daneDane.wrzucTrocheDanychDoBazy(daneDane.getPokoje(), daneDane.getMieszkancy());
		logg.trace("----->>> dane wrzucone do bazy");
		List<Pokoj> pokList = pokojRepo.findByNumerPokoju("102");
		logg.trace("----->>> lista pokoi pobrana");
		Assert.assertEquals(3, pokList.get(0).getLiczbaMiejsc());
	}

	
	@Test
	public void testWrzucTrocheDanychDoBazy() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazy start");
		daneDane.wrzucTrocheDanychDoBazy();
		logg.trace("-------+> dane wrzucone");
		long ilePokoi = pokojRepo.count();
		Assert.assertEquals("Liczba pokoi niezgodna", 3, ilePokoi);
		long ileStudentow = studentRepo.count();
		Assert.assertEquals("Liczba studnetow niezgodna", 6, ileStudentow);
	}
}
