package org.wojtekz.akademik.namedbean;

import java.util.List;

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
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Test beana JSF wyświetlającego Studentów.
 * 
 * @author Wojciech Zaręba
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class StudentBeanTest {
	
	@Autowired
	private transient PokojService pokojService;
	
	@Autowired
	private transient StudentBean studentBean;
	
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
	public void testPobierzStudentow() {
		List<String> stntStrList = studentBean.pobierzStudentow();
		Assert.assertEquals(6, stntStrList.size());
		Assert.assertEquals("Student [id=3, imie=Adam, nazwisko=Malinowski, plec=MEZCZYZNA]", stntStrList.get(2));
	}

}
