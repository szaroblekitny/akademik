package org.wojtekz.akademik.namedbean;

import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
import org.wojtekz.akademik.util.DaneTestowe;


/**
 * Test beana JSF Zakwateruj.
 * 
 * @author Wojciech Zaręba
 *
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
@WebAppConfiguration
public class ZakwaterujBeanTest {
	private static Logger logg = LogManager.getLogger();
	private final static long PIERSZY = 1L;
	
	private transient ZakwaterujBean zakwaterujBean;
	private transient DaneTestowe daneTestowe;
	private transient Messagesy komunikaty;
	private StudentRepository studentRepo;
	private PokojRepository pokojRepo;
	
	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	@Autowired
	public void setPokojRepo(PokojRepository pokojRepo) {
		this.pokojRepo = pokojRepo;
	}
	
	@Autowired
	public void setZakwaterujBean(ZakwaterujBean zakwaterujBean) {
		logg.debug("-------> setZakwaterujBean {}", zakwaterujBean);
		this.zakwaterujBean = zakwaterujBean;
	}
	
	@Autowired
	public void setDaneTestowe(DaneTestowe daneTestowe) {
		this.daneTestowe = daneTestowe;
	}
	
	
	@BeforeEach
	public void setUp() throws Exception {
		logg.debug("-------> setUp ZakwaterujBeanTest");
		
		komunikaty = mock(Messagesy.class);
		zakwaterujBean.setMessagesy(komunikaty);
		daneTestowe.wrzucTrocheDanychDoBazy();
		logg.debug("-------> setUp ZakwaterujBeanTest koniec");
	}

	@AfterEach
	public void tearDown() throws Exception {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
	}

	/**
	 * Test funkcji kwaterowania. Pobiera studenta o ID=1,
	 * sprawdza, czy ma on uzupełnione pole pokój i sprawdza,
	 * czy jest to właściwy pokój.
	 * 
	 */
	@Test
	public void testZakwateruj() {
		logg.debug("=============>>> testZakwateruj");
		Assertions.assertNotNull(zakwaterujBean);
		Optional<Student> studenPrzed = studentRepo.findById(PIERSZY);

		Assertions.assertFalse(studenPrzed.isEmpty(), "----> Nie ma studenta");
		
		Student testStudent = studenPrzed.get();
		
		Assertions.assertNull(testStudent.getPokoj());
		
		zakwaterujBean.zakwateruj();
		testStudent = studentRepo.findById(PIERSZY).get();
		
		Assertions.assertNotNull(testStudent.getPokoj());
		Assertions.assertEquals(2L, testStudent.getPokoj().getId());
	}

}
