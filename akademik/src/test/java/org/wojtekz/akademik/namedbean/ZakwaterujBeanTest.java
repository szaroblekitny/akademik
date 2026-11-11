package org.wojtekz.akademik.namedbean;

import static org.mockito.Mockito.mock;

import java.util.List;
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
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
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


	/**
	 * Krok 2.7 (issue #16): strona główna pokazuje studentów według numerów pokoi,
	 * w pokoju według nazwisk, a studentów bez pokoju na końcu.
	 * <p>
	 * Dane są dobrane tak, żeby wychwycić dwa błędy:
	 * <ul>
	 * <li>kolejność ID (jak w {@code findAll()}) jest inna niż oczekiwana;</li>
	 * <li>zwykłe {@code String.compareTo} porównuje kody znaków, więc postawiłoby
	 *     „Tarka” przed „Świerk” i „Mak” przed „Łoś”; polska kolejność liter
	 *     ({@code Collator}) daje odwrotnie.</li>
	 * </ul>
	 */
	@Test
	public void testKolejnosciNaStronieGlownej() {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
		Pokoj pokoj102 = pokoj(1, "102");
		Pokoj pokoj101 = pokoj(2, "101");
		pokojRepo.saveAll(List.of(pokoj102, pokoj101));
		studentRepo.saveAll(List.of(
				student(1, "Jan", "Zając", pokoj102),
				student(2, "Ewa", "Tarka", pokoj101),
				student(3, "Adam", "Mak", null),
				student(4, "Piotr", "Świerk", pokoj101),
				student(5, "Olek", "Łoś", null)));

		List<String> kolejnosc = zakwaterujBean.getStudenci().stream()
				.map(Student::getNazwisko)
				.toList();

		Assertions.assertEquals(List.of("Świerk", "Tarka", "Zając", "Łoś", "Mak"), kolejnosc);
	}

	private static Pokoj pokoj(long id, String numer) {
		Pokoj pokoj = new Pokoj();
		pokoj.setId(id);
		pokoj.setNumerPokoju(numer);
		pokoj.setLiczbaMiejsc(3);
		return pokoj;
	}

	private static Student student(long id, String imie, String nazwisko, Pokoj pokoj) {
		Student student = new Student();
		student.setId(id);
		student.setImie(imie);
		student.setNazwisko(nazwisko);
		student.setPlec(Plec.MEZCZYZNA);
		student.setPokoj(pokoj);
		return student;
	}
}
