package org.wojtekz.akademik.util;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
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
@SpringJUnitConfig(classes = {TestConfiguration.class})
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

	@AfterEach
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
		Assertions.assertFalse(stt.isEmpty());
		Assertions.assertEquals("Malinowski", stt.get().getNazwisko());
	}

	@Test
	public void testWrzucTrocheDanychDoBazyListOfPokojListOfStudent() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazyListOfPokojListOfStudent start");
		daneDane.wrzucTrocheDanychDoBazy(daneDane.getPokoje(), daneDane.getMieszkancy());
		logg.trace("----->>> dane wrzucone do bazy");
		List<Pokoj> pokList = pokojRepo.findByNumerPokoju("102");
		logg.trace("----->>> lista pokoi pobrana");
		Assertions.assertEquals(3, pokList.get(0).getLiczbaMiejsc());
	}

	
	@Test
	public void testWrzucTrocheDanychDoBazy() {
		logg.debug("----->>> testWrzucTrocheDanychDoBazy start");
		daneDane.wrzucTrocheDanychDoBazy();
		logg.trace("-------+> dane wrzucone");
		long ilePokoi = pokojRepo.count();
		Assertions.assertEquals(3, ilePokoi, "Liczba pokoi niezgodna");
		long ileStudentow = studentRepo.count();
		Assertions.assertEquals(6, ileStudentow, "Liczba studnetow niezgodna");
	}
}
