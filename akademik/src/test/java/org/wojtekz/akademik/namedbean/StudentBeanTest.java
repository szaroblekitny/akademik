package org.wojtekz.akademik.namedbean;

import static org.mockito.Mockito.*;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Student;
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
	private static Logger logg = LogManager.getLogger();
	private final static String KLAPA = "Klapaucjusz";
	private final static String SMIESZNY = "Śmieszny";
	
	private transient StudentBean studentBean;
	private transient DaneTestowe daneTestowe;
	private transient Messagesy komunikaty;
	private transient UIComponent component;
	private transient Behavior behavior;
	private Student student;
	
	
	@Autowired
	public void setStudentBean(StudentBean studentBean) {
		logg.debug("-------> setStudentBean");
		this.studentBean = studentBean;
	}

	@Autowired
	public void setDaneTestowe(DaneTestowe daneTestowe) {
		this.daneTestowe = daneTestowe;
	}


	@Before
	public void setUp() throws Exception {
		logg.debug("-------> setUp");
		komunikaty = mock(Messagesy.class);
		component = mock(UIComponent.class);
		behavior = mock(Behavior.class);
		studentBean.setMessagesy(komunikaty);
		daneTestowe.wrzucTrocheDanychDoBazy();
		student = new Student();
		student.setId(10);
		student.setImie(KLAPA);
		student.setNazwisko(SMIESZNY);
	}

	@After
	public void tearDown() throws Exception {
		// TO DO pokojService.deleteAll();
		// TO DO studentService.deleteAll();
	}

	@Test
	public void testPobierzStudentow() {
		logg.debug("-------> testPobierzStudentow");
		List<String> stntStrList = studentBean.pobierzStudentow();
		Assert.assertEquals(6, stntStrList.size());
		Assert.assertEquals("Student [id=3, imie=Adam, nazwisko=Malinowski, plec=MEZCZYZNA]", stntStrList.get(2));
	}
	
	@Test
	public void testOnRowEdit() {
		logg.debug("-------> testOnRowEdit");
		studentBean.onRowEdit(new RowEditEvent(component, behavior, student));
		verify(komunikaty).addMessage("Edycja studenta", "Student o Id 10");
		// TO DO Student sprStudent = studentService.findById(10);
		// TO DO Assert.assertEquals(KLAPA, sprStudent.getImie());
	}
	
	@Test
	public void testOnRowCancel() {
		logg.debug("-------> testOnRowCancel");
		studentBean.onRowCancel(new RowEditEvent(component, behavior, student));
		verify(komunikaty).addMessage("Edycja anulowana", "10");
	}
	
	@Test
	public void testGetStudenci() {
		logg.debug("-------> testGetStudenci");
		// TO DO Assert.assertEquals(6, studentService.listAll().size());
		// TO DO StudentBean bean = new StudentBean(studentService);
		// TO DO List<Student> stLi = bean.getStudenci();
		// TO DO Assert.assertEquals(6, stLi.size());
	}
 
}
