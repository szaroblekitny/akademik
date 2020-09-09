package org.wojtekz.akademik.repo;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class StudentRepositoryTest {
	private static Logger logg = LogManager.getLogger();
	private Student student;
	private StudentRepository studentRep;
	private PokojRepository pokojRep;
	private final static String KOWALSKA = "Kowalska";
	private final static String KASIA = "Kasia";
	
	@Autowired
	public void setStudentRep(StudentRepository studentRep) {
		this.studentRep = studentRep;
	}

	@Autowired
	public void setPokojRep(PokojRepository pokojRep) {
		this.pokojRep = pokojRep;
	}

	@Before
	public void before() {
		logg.debug("----->>> StudentRepository before method fired");
		student = new Student();
		student.setId(1);
		student.setImie(KASIA);
		student.setNazwisko(KOWALSKA);
		student.setPlec(Plec.KOBIETA);
	}

	/**
	 * Metoda testuje tylko, czy zwracany obiekt nie jest nulem. Wśaściwie służy jedynie
	 * do przetestowania całego ciągu persystencji dla klasy Student. W tym teście
	 * zadnymi danymi się nie przejmujemy. Tak napradwdą sprawdzane jest, czy cokolwiek
	 * jest zwracane po przejściu wszystkich stopni połączenia z bazą danych. 
	 */
	@Test
	public void findAllTest() {
		logg.debug("----->>> findAllTest method fired");
		Assert.assertNotNull(studentRep.findAll());
	}
	
	/**
	 * Metoda kasuje wszystkie rekordy, zapisuje naszego studenta do bazy
	 * i wyszukuje wszystkich studentów,
	 * co ma dać niepusty zbiór studentów
	 */
	@Test
	public void zapiszIOdczytay() {
		logg.debug("----->>> zapiszIOdczytay method fired");
		
		studentRep.deleteAll();
		logg.trace("----->>> zapiszIOdczytay po deleteAll");
		studentRep.save(student);
		logg.trace("----->>> zapiszIOdczytay po save student");
		List<Student> listaStudentow = studentRep.findAll();
		logg.trace("----->>> zapiszIOdczytay po findAll");
		Assert.assertEquals(1, listaStudentow.size());
		Assert.assertEquals(KASIA, listaStudentow.get(0).getImie());
		Assert.assertEquals(Plec.KOBIETA, listaStudentow.get(0).getPlec());
	}
	
	@Test
	public void testFindByName() {
		studentRep.save(student);
		List<Student> listaStudentow = studentRep.findByName(KOWALSKA);
		Assert.assertEquals(KASIA, listaStudentow.get(0).getImie());
	}
	
	/**
	 * Test wyszukiwania po numerze pokoju.
	 */
	@Test
	public void testFindByPokoj () {
		Pokoj zamieszkaly = new Pokoj();
		zamieszkaly.setId(1L);
		zamieszkaly.setLiczbaMiejsc(2);
		zamieszkaly.setNumerPokoju("101");
		zamieszkaly.zakwateruj(student);
		pokojRep.save(zamieszkaly);
		studentRep.save(student);
		List<Student> ciZPokoju = studentRep.findByPokoj(zamieszkaly);
		Assert.assertEquals(KASIA, ciZPokoju.get(0).getImie());
	}

}
