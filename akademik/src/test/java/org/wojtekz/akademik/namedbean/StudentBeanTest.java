package org.wojtekz.akademik.namedbean;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.Behavior;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Plec;
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
@SpringJUnitConfig(classes = {TestConfiguration.class})
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


	@BeforeEach
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

	@AfterEach
	public void tearDown() throws Exception {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
		
	}


	@Test
	public void testOnRowEdit() {
		logg.debug("===========> testOnRowEdit");
		studentBean.onRowEdit(new RowEditEvent<Student>(component, behavior, student));
		verify(komunikaty).addMessage("Edycja studenta",
				"Zapisany Student [id=10, imie=" + KLAPA
				+ ", nazwisko=" + SMIESZNY + ", plec=null, nr pokoju=niezakw.]");
		Optional<Student> sprStudent = studentRepo.findById(10L);
		Assertions.assertTrue(sprStudent.isPresent());
		Assertions.assertEquals(KLAPA, sprStudent.get().getImie());
	}

	@Test
	public void testOnRowCancel() {
		logg.debug("===========> testOnRowCancel");
		studentBean.onRowCancel(new RowEditEvent<Student>(component, behavior, student));
		verify(komunikaty).addMessage("Edycja anulowana", "10");
	}

	/**
	 * Błąd zgłoszony 11.11.2026: formularz pozwalał dodać studenta bez wybranej płci,
	 * a taki student psuje kwaterowanie, eksport i raporty. Ma nie trafić do bazy,
	 * a ekran ma dostać komunikat błędu.
	 */
	@Test
	public void testDodaniaStudentaBezPlci() {
		studentBean.getStudenci();
		long przed = studentRepo.count();
		studentBean.setImie("Gerwazy");
		studentBean.setNazwisko("Kapuściński");
		studentBean.setPlec(null);

		studentBean.onAddNew();

		Assertions.assertEquals(przed, studentRepo.count(), "Student bez płci nie może trafić do bazy");
		verify(komunikaty).addMessage(eq(FacesMessage.SEVERITY_ERROR), eq("Tworzenie"), contains("płeć"), isNull());
	}

	/**
	 * Błąd zgłoszony 11.11.2026: dało się zapisać studenta bez imienia albo nazwiska.
	 * Same spacje też się nie liczą: schemat pliku danych ({@code xs:token}) usuwa je
	 * i wymaga co najmniej jednego znaku, więc taki student psułby potem eksport.
	 */
	@Test
	public void testDodaniaStudentaBezImienia() {
		studentBean.getStudenci();
		long przed = studentRepo.count();
		studentBean.setImie(null);
		studentBean.setNazwisko("Kapuściński");
		studentBean.setPlec(Plec.MEZCZYZNA);

		studentBean.onAddNew();

		Assertions.assertEquals(przed, studentRepo.count(), "Student bez imienia nie może trafić do bazy");
		verify(komunikaty).addMessage(eq(FacesMessage.SEVERITY_ERROR), eq("Tworzenie"), contains("imię"), isNull());
	}

	@Test
	public void testDodaniaStudentaBezNazwiska() {
		studentBean.getStudenci();
		long przed = studentRepo.count();
		studentBean.setImie("Gerwazy");
		studentBean.setNazwisko("   ");
		studentBean.setPlec(Plec.MEZCZYZNA);

		studentBean.onAddNew();

		Assertions.assertEquals(przed, studentRepo.count(), "Student z samymi spacjami w nazwisku nie może trafić do bazy");
		verify(komunikaty).addMessage(eq(FacesMessage.SEVERITY_ERROR), eq("Tworzenie"), contains("nazwisko"), isNull());
	}

	/** To samo przy edycji wiersza: pusty wpis nie nadpisuje danych w bazie. */
	@Test
	public void testEdycjiStudentaBezNazwiska() {
		student.setNazwisko("");

		studentBean.onRowEdit(new RowEditEvent<Student>(component, behavior, student));

		Assertions.assertTrue(studentRepo.findById(10L).isEmpty(), "Student z pustym nazwiskiem nie może trafić do bazy");
		verify(komunikaty).addMessage(eq(FacesMessage.SEVERITY_ERROR), eq("Edycja studenta"), contains("nazwisko"), isNull());
	}

	@Test
	public void testGetStudenci() {
		logg.debug("===========> testGetStudenci");
		Assertions.assertEquals(6, studentRepo.findAll().size());
		StudentBean bean = new StudentBean();
		Assertions.assertNotNull(bean);
		bean.setStudentRepo(studentRepo);
		List<Student> stLi = bean.getStudenci();
		Assertions.assertEquals(6, stLi.size());
	}

}
