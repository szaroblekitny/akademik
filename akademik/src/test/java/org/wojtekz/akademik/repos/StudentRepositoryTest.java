package org.wojtekz.akademik.repos;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Student;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class StudentRepositoryTest {
	private static Logger logg = Logger.getLogger(StudentRepositoryTest.class.getName());
	private Student student;
	
	@Autowired
	private StudentRepository studentRep;
	
	@Before
	public void before() {
		logg.debug("----->>> before method fired");
		student = new Student();
		student.setId(1);
	}

	/**
	 * Metoda testuje tylko, czy zwracany obiekt nie jest nulem. W�a�ciwie s�u�y
	 * do przetestowania ca�ego ci�gu persystencji dla klasy Student. W tym te�cie
	 * �adnymi danymi si� nie przejmujemy. Tak napradwd� sprawdzane jest, czy cokolwiek
	 * jest zwracane po przej�ciu wszystkich stopni po��czenia z baz� danych. 
	 */
	@Test
	public void findAllTest() {
		logg.debug("----->>> findAllTest method fired");
		Assert.assertNotNull(studentRep.findAll());
	}
	
	/**
	 * Metoda kasuje wszystkie rekordy, zapisuje naszego studenta do bazy
	 * i wyszukuje wszystkich student�w,
	 * co ma da� niepusty zbi�r student�w
	 */
	@Test
	public void zapiszIOdczytay() {
		List<Student> listaStudentow;
		logg.debug("----->>> zapiszIOdczytay method fired");
		studentRep.deleteAll();
		logg.debug("----->>> zapiszIOdczytay po deleteAll");
		// public <S extends Student> S save(S entity) {
		studentRep.save(student);
		logg.debug("----->>> zapiszIOdczytay po save student");
		listaStudentow = studentRep.findAll();
		logg.debug("----->>> zapiszIOdczytay po findAll");
		Assert.assertEquals(1, listaStudentow.size());
		
	}

}
