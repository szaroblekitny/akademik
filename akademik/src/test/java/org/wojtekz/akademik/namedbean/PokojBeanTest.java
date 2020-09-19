package org.wojtekz.akademik.namedbean;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
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
	private transient UIComponent component = mock(UIComponent.class);
	private transient Behavior behavior = mock(Behavior.class);
	

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

	@Before
	public void setUp() throws Exception {
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
		Assert.assertNotNull("PokBean nullem getPok", testowanyBean);
		List<Pokoj> lista = testowanyBean.getPokoje();
		Assert.assertEquals(3, lista.size());
		Assert.assertEquals("200", lista.get(1).getNumerPokoju());
	}
	
	@Test
	public void testPobierzPokoje() {
		logg.debug("===========> testPobierzPokoje");
		Assert.assertEquals(3, pokojRepository.count());
		Assert.assertNotNull("PokBean nullem pobPok", testowanyBean);
		List<String> pokStrList = testowanyBean.pobierzPokoje();
		Assert.assertEquals(3, pokStrList.size());
		Assert.assertEquals("Pokoj [id=500, numerPokoju=500, liczbaMiejsc=500]", pokStrList.get(2));
	}
	
	@Test
	public void testOnRowEdit() {
		logg.debug("===========> testOnRowEdit");
		Assert.assertNotNull("PokBean nullem (edit)", testowanyBean);
		Assert.assertNotNull("Pokój nullem", pokoik500);
		Assert.assertNotNull("Komponent nullem", component);
		Assert.assertNotNull("Zachowanie nullem", behavior);
		logg.debug("-------> wywołanie OnRowEdit");
		testowanyBean.onRowEdit(new RowEditEvent<Pokoj>(component, behavior, pokoik500));
		verify(komunikaty).addMessage("Edycja", "Zapisany Pokoj [id=500, numerPokoju=500, liczbaMiejsc=500]");
		Assert.assertEquals("500", pokoik500.getNumerPokoju());
	}
	
	@Test
	public void testOnRowCancel() {
		logg.debug("===========> testOnRowCancel");
		Assert.assertNotNull("Pokój nullem (canc.)", pokoik500);
		Assert.assertNotNull("PokBean nullem (cancel)", testowanyBean);
		testowanyBean.onRowCancel(new RowEditEvent<Pokoj>(component, behavior, pokoik500));
		verify(komunikaty).addMessage("Edycja anulowana", "500");
	}

	// --------------------------------------------

	@After
	public void tearDown() throws Exception {
		logg.debug("---------+> tearDown PokojBeanTest");
		studentRepo.deleteAll();
		pokojRepository.deleteAll();
	}

	

}
