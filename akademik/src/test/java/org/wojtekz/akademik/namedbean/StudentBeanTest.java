package org.wojtekz.akademik.namedbean;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
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
	private transient StudentRepository studentRepo;
	private transient PokojRepository pokojRepo;
	
	private transient Messagesy komunikaty;
	private transient UIComponent component;
	private transient Behavior behavior;
	private transient Student student;
	
	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	@Autowired
	public void setPokojRepo(PokojRepository pokojRepo) {
		this.pokojRepo = pokojRepo;
	}
	
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
		logg.debug("------+> setUp");
		
		// sprawdzenie, czy nie zostały pokoje z poprzedniego testu
		// i ewentualne kasowanie
		List<Pokoj> pokoje;
		long ile = pokojRepo.count();
		if (ile > 0L) {
			pokoje = pokojRepo.findAll();
			logg.trace("Mamy pokoje ---------+> {}", Arrays.toString(pokoje.toArray()));
			studentRepo.deleteAll();
			pokojRepo.deleteAll();
		}

		komunikaty = mock(Messagesy.class);
		component = mock(UIComponent.class);
		behavior = mock(Behavior.class);
		studentBean.setMessagesy(komunikaty);

		daneTestowe.wrzucTrocheDanychDoBazy();
		
		student = new Student();
		student.setId(10);
		student.setImie(KLAPA);
		student.setNazwisko(SMIESZNY);
		logg.debug("------+> koniec setUp");
	}

	@After
	public void tearDown() throws Exception {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
		
	}

	@Test
	public void testPobierzStudentow() {
		logg.debug("===========> testPobierzStudentow");
		List<String> stntStrList = studentBean.pobierzStudentow();
		Assert.assertEquals(6, stntStrList.size());
		Assert.assertEquals("Student [id=3, imie=Adam, nazwisko=Malinowski, plec=MEZCZYZNA, nr pokoju=niezakw.]", stntStrList.get(2));
	}

	@Test
	public void testOnRowEdit() {
		logg.debug("===========> testOnRowEdit");
		studentBean.onRowEdit(new RowEditEvent<Student>(component, behavior, student));
		verify(komunikaty).addMessage("Edycja studenta",
				"Zapisany Student [id=10, imie=" + KLAPA
				+ ", nazwisko=" + SMIESZNY + ", plec=null, nr pokoju=niezakw.]");
		Optional<Student> sprStudent = studentRepo.findById(10L);
		Assert.assertTrue(sprStudent.isPresent());
		Assert.assertEquals(KLAPA, sprStudent.get().getImie());
	}

	@Test
	public void testOnRowCancel() {
		logg.debug("===========> testOnRowCancel");
		studentBean.onRowCancel(new RowEditEvent<Student>(component, behavior, student));
		verify(komunikaty).addMessage("Edycja anulowana", "10");
	}

	@Test
	public void testGetStudenci() {
		logg.debug("===========> testGetStudenci");
		Assert.assertEquals(6, studentRepo.findAll().size());
		StudentBean bean = new StudentBean();
		Assert.assertNotNull(bean);
		bean.setStudentRepo(studentRepo);
		List<Student> stLi = bean.getStudenci();
		Assert.assertEquals(6, stLi.size());
	}

}
