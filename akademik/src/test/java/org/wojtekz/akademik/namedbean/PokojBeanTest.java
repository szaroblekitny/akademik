package org.wojtekz.akademik.namedbean;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.component.behavior.Behavior;
import jakarta.faces.component.behavior.BehaviorBase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

/**
 * Test beana JSF dla pokoi. Generalnie chodzi o to, że Sonar
 * bardzo sugeruje testować też takie klasy.
 * 
 * @author Wojciech Zaręba
 *
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
@WebAppConfiguration
public class PokojBeanTest {
	private static Logger logg = LogManager.getLogger();
	
	private transient Pokoj pokoik100;
	private transient Pokoj pokoik200;
	private transient Pokoj pokoik500;
	private transient Student student;
	private transient ArrayList<Student> studenty = new ArrayList<>();
	
	
	private transient PokojBean testowanyBean;
	private transient PokojRepository pokojRepository;
	private transient StudentRepository studentRepo;
	private transient Messagesy komunikaty = mock(Messagesy.class);
	private transient static UIComponent component;
	private transient static Behavior behavior;
	

	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}
	
	@Autowired
	public void setPokojRepository(PokojRepository pokojRepository) {
		this.pokojRepository = pokojRepository;
	}
	
	@Autowired
	public void setTestowanyBean(PokojBean testowanyBean) {
		this.testowanyBean = testowanyBean;
	}

	@BeforeAll
	static void setup() {
		class TestComponent extends UIComponentBase {
	        @Override
	        public String getFamily() {
	            return "test";
	        }
	    };

	    component = new TestComponent();
	    behavior = new BehaviorBase();
	}

	@BeforeEach
	void init() throws Exception {
		logg.debug("---------+> setUp PokojBeanTest");
		studentRepo.deleteAll();
		// sprawdzenie, czy nie zostały pokoje z poprzedniego testu
		// i ewentualne kasowanie
		long ile = pokojRepository.count();
		if (ile > 0L) {
			List<Pokoj> pokoje = pokojRepository.findAll();
			logg.trace("Mamy pokoje ------+> {}", Arrays.toString(pokoje.toArray()));
			pokojRepository.deleteAll();
		}

		testowanyBean.setMessagesy(komunikaty);
		
		pokoik100 = new Pokoj();
		pokoik100.setId(100L);
		pokoik100.setNumerPokoju("100");
		pokoik100.setLiczbaMiejsc(100);
		pokoik100.setZakwaterowani(new ArrayList<Student>());
		pokojRepository.save(pokoik100);
		
		pokoik200 = new Pokoj();
		pokoik200.setId(200L);
		pokoik200.setNumerPokoju("200");
		pokoik200.setLiczbaMiejsc(200);
		pokoik200.setZakwaterowani(new ArrayList<Student>());
		pokojRepository.save(pokoik200);
		
		student = new Student();
		student.setId(1L);
		student.setImie("Jan");
		student.setNazwisko("Nowakowaski");
		student.setPlec(Plec.MEZCZYZNA);
		studentRepo.save(student);
		
		studenty.add(student);
		
		pokoik500 = new Pokoj();
		pokoik500.setId(500L);
		pokoik500.setNumerPokoju("500");
		pokoik500.setLiczbaMiejsc(500);
		pokoik500.setZakwaterowani(studenty);
		pokojRepository.save(pokoik500);

	}

	// --------------------------------------------
	
	@Test
	public void testGetPokoje() {
		logg.debug("===========> testGetPokoje");
		Assertions.assertNotNull(testowanyBean, "PokBean nullem getPok");
		List<Pokoj> lista = testowanyBean.getPokoje();
		Assertions.assertEquals(3, lista.size());
		Assertions.assertEquals("200", lista.get(1).getNumerPokoju());
	}
	
	@Test
	public void testPobierzPokoje() {
		logg.debug("===========> testPobierzPokoje");
		Assertions.assertEquals(3, pokojRepository.count());
		Assertions.assertNotNull(testowanyBean, "PokBean nullem pobPok");
		List<String> pokStrList = testowanyBean.pobierzPokoje();
		Assertions.assertEquals(3, pokStrList.size());
		Assertions.assertEquals("Pokoj [id=500, numerPokoju=500, liczbaMiejsc=500]", pokStrList.get(2));
	}
	
	@Test
	public void testOnRowEdit() {
		logg.debug("===========> testOnRowEdit");
		Assertions.assertNotNull(testowanyBean, "PokBean nullem (edit)");
		Assertions.assertNotNull(pokoik500);
		Assertions.assertNotNull(component, "Komponent nullem");
		Assertions.assertNotNull(behavior, "Zachowanie nullem");
		logg.debug("-------> wywołanie OnRowEdit");
		testowanyBean.onRowEdit(new RowEditEvent<Pokoj>(component, behavior, pokoik500));
		verify(komunikaty).addMessage("Edycja", "Zapisany Pokoj [id=500, numerPokoju=500, liczbaMiejsc=500]");
		Assertions.assertEquals("500", pokoik500.getNumerPokoju());
	}
	
	@Test
	public void testOnRowCancel() {
		logg.debug("===========> testOnRowCancel");
		Assertions.assertNotNull(pokoik500, "Pokój nullem (canc.)");
		Assertions.assertNotNull(testowanyBean, "PokBean nullem (cancel)");
		testowanyBean.onRowCancel(new RowEditEvent<Pokoj>(component, behavior, pokoik500));
		verify(komunikaty).addMessage("Edycja anulowana", "500");
	}

	@Test
	public void testOnAddNew() {
		logg.debug("===========> testOnAddNew");
		Assertions.assertNotNull(testowanyBean, "PokBean nullem (add new)");
		testowanyBean.getPokoje();
		testowanyBean.setNumerPokoju("102");
		testowanyBean.setLiczbaMiejsc(20);
		testowanyBean.onAddNew();
		Assertions.assertEquals(4, pokojRepository.count());
	}
	
	// Próbowałem napisać testDeletePokoj, ale wtedy trzeba zastosować zaawansowane
	// metody mockowania różnych aspektów PrimeFaces i w końcu dałem spokój.


	// --------------------------------------------

	@AfterEach
	public void tearDown() throws Exception {
		logg.debug("---------+> tearDown PokojBeanTest");
		studentRepo.deleteAll();
		pokojRepository.deleteAll();
	}

}
