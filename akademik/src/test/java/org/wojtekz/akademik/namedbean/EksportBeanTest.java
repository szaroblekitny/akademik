package org.wojtekz.akademik.namedbean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.core.Plikowanie;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.plik.DaneZPliku;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Test beana eksportu (krok 2.2 planu naprawczego). Bean działa na prawdziwej
 * usłudze {@code DaneService} i testowej bazie HSQLDB, a pobrany plik czytamy
 * z powrotem przez {@link Plikowanie#wczytajDane}.
 * <p>
 * {@link WebAppConfiguration}: bean ma zasięg sesji, więc test potrzebuje
 * kontekstu webowego Springa (z udawaną sesją HTTP).
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
@WebAppConfiguration
class EksportBeanTest {

	@Autowired
	private EksportBean eksportBean;

	@Autowired
	private Plikowanie plikowanie;

	@Autowired
	private PokojRepository pokojRepo;

	@Autowired
	private StudentRepository studentRepo;

	@BeforeEach
	@AfterEach
	void wyczyscBaze() {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
	}

	@Test
	void testEksportuDoPliku() throws IOException {
		pokojRepo.saveAll(List.of(pokoj(1, "101", 2), pokoj(2, "102", 3)));
		studentRepo.save(student(1, "Łucja", "Żółć", Plec.KOBIETA));

		StreamedContent plik = eksportBean.getContent();

		Assertions.assertNotNull(plik, "Bean powinien dać plik do pobrania");
		Assertions.assertEquals("akademik.xml", plik.getName());
		Assertions.assertEquals("application/xml", plik.getContentType());
		DaneZPliku dane = przeczytaj(plik);
		Assertions.assertEquals(List.of("101", "102"),
				dane.pokoje().stream().map(Pokoj::getNumerPokoju).toList());
		Assertions.assertEquals("Żółć", dane.studenci().get(0).getNazwisko());
	}

	/**
	 * Dawny bean liczył plik raz, w konstruktorze, czyli raz na sesję. Teraz każde
	 * kliknięcie ma dawać aktualny stan bazy.
	 */
	@Test
	void testEksportuAktualnychDanych() throws IOException {
		pokojRepo.save(pokoj(1, "101", 2));
		Assertions.assertEquals(1, przeczytaj(eksportBean.getContent()).pokoje().size());

		pokojRepo.save(pokoj(2, "102", 3));

		Assertions.assertEquals(2, przeczytaj(eksportBean.getContent()).pokoje().size(),
				"Drugi eksport w tej samej sesji powinien zobaczyć nowy pokój");
	}

	// ----- pomocnicze -----

	private DaneZPliku przeczytaj(StreamedContent plik) throws IOException {
		try (InputStream in = plik.getStream().get()) {
			return plikowanie.wczytajDane(in);
		}
	}

	private static Pokoj pokoj(long id, String numer, int miejsc) {
		Pokoj pokoj = new Pokoj();
		pokoj.setId(id);
		pokoj.setNumerPokoju(numer);
		pokoj.setLiczbaMiejsc(miejsc);
		return pokoj;
	}

	private static Student student(long id, String imie, String nazwisko, Plec plec) {
		Student student = new Student();
		student.setId(id);
		student.setImie(imie);
		student.setNazwisko(nazwisko);
		student.setPlec(plec);
		return student;
	}
}
