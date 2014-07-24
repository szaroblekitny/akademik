package org.wojtekz.akademik.dao;

import org.apache.log4j.Logger;
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
//ApplicationContext will be loaded from AppConfig and TestConfig
//@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class StudentDaoImplTest {
	private static Logger logg = Logger.getLogger(StudentDaoImplTest.class.getName());
	private Student student;
	
	@Autowired
	private StudentDao studentDao = new StudentDaoImpl();
	
	@Before
	public void before() {
		logg.debug("----->>> before method fired");
		student = new Student();
		student.setId(1);
	}

	/**
	 * Metoda testuje tylko, czy zwracany obiekt nie jest nulem. W³aœciwie s³u¿y
	 * do przetestowania ca³ego ci¹gu persystencji dla klasy Student. W tym teœcie
	 * ¿adnymi danymi siê nie przejmujemy. Tak napradwdê sprawdzane jest, czy cokolwiek
	 * jest zwracane po przejœciu wszystkich stopni po³¹czenia z baz¹ danych. 
	 */
	@Test
	public void findAllTest() {
		logg.debug("----->>> findAllTest method fired");
		Assert.assertNotNull(studentDao.findAll());
	}

}
