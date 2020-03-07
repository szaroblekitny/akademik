package org.wojtekz.akademik.dao;

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
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Student;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class StudentRepositoryTest {
	private static Logger logg = LogManager.getLogger();
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
	 * Metoda testuje tylko, czy zwracany obiekt nie jest nulem. Właściwie służy
	 * do przetestowania całego ciągu persystencji dla klasy Student. W tym teście
	 * żadnymi danymi się nie przejmujemy. Tak napradwdę sprawdzane jest, czy cokolwiek
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
