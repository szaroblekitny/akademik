package org.wojtekz.akademik.namedbean;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.faces.application.FacesMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
import org.wojtekz.akademik.service.RaportService;

/**
 * Test beana raportu (krok 2.4 planu naprawczego): plik do pobrania ma właściwą
 * nazwę, typ i treść. Sam raport sprawdza {@code RaportServiceTest}.
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
@WebAppConfiguration
class RaportBeanTest {

	@Autowired
	private RaportBean raportBean;

	@Autowired
	private PokojRepository pokojRepo;

	@Autowired
	private StudentRepository studentRepo;

	@AfterEach
	void wyczyscBaze() {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
	}

	@Test
	void testRaportuDoPobrania() throws IOException {
		Pokoj pokoj = new Pokoj();
		pokoj.setId(1);
		pokoj.setNumerPokoju("101");
		pokoj.setLiczbaMiejsc(2);
		pokojRepo.save(pokoj);

		StreamedContent plik = raportBean.getRaportXml();

		Assertions.assertNotNull(plik, "Bean powinien dać plik do pobrania");
		Assertions.assertEquals("raport-kwaterunku.xml", plik.getName());
		Assertions.assertEquals("application/xml", plik.getContentType());
		try (InputStream in = plik.getStream().get()) {
			String xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			Assertions.assertTrue(xml.contains("numer=\"101\""), xml);
		}
	}

	/**
	 * Błąd w danych nie może przenieść użytkownika na stronę błędu: bean zwraca
	 * {@code null} (wtedy {@code p:fileDownload} niczego nie wysyła) i dodaje komunikat.
	 */
	@Test
	void testRaportuZeStudentemBezPlci() {
		Student gerwazy = new Student();
		gerwazy.setId(1);
		gerwazy.setImie("Gerwazy");
		gerwazy.setNazwisko("Kapuściński");
		studentRepo.save(gerwazy);
		Messagesy komunikaty = mock(Messagesy.class);
		raportBean.setMessagesy(komunikaty);

		Assertions.assertNull(raportBean.getRaportXml(), "Bez pliku do pobrania");
		Assertions.assertNull(raportBean.getRaportHtml(), "Bez pliku do pobrania");

		verify(komunikaty, times(2)).addMessage(eq(FacesMessage.SEVERITY_ERROR), eq("Raport"),
				contains("Gerwazy Kapuściński"), isNull());
	}

	@Test
	void testRaportuHtmlDoPobrania() throws IOException {
		Pokoj pokoj = new Pokoj();
		pokoj.setId(1);
		pokoj.setNumerPokoju("101");
		pokoj.setLiczbaMiejsc(2);
		pokojRepo.save(pokoj);

		StreamedContent plik = raportBean.getRaportHtml();

		Assertions.assertNotNull(plik, "Bean powinien dać plik do pobrania");
		Assertions.assertEquals("raport-kwaterunku.html", plik.getName());
		Assertions.assertEquals("text/html", plik.getContentType());
		try (InputStream in = plik.getStream().get()) {
			String html = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			Assertions.assertTrue(html.contains("<td>101</td>"), html);
		}
	}

	/**
	 * Krok 2.6: gruby błąd usługi raportu (udawana mockiem awaria bazy) ma wyjść
	 * z beana, żeby JSF przeniósł użytkownika na stronę błędu {@code errors.xhtml}.
	 */
	@Test
	void testGrubegoBleduRaportu() {
		RaportService awaria = mock(RaportService.class);
		when(awaria.zapiszRaport(any())).thenThrow(new IllegalStateException("awaria bazy"));
		raportBean.setRaportService(awaria);
		Messagesy komunikaty = mock(Messagesy.class);
		raportBean.setMessagesy(komunikaty);

		Assertions.assertThrows(IllegalStateException.class, () -> raportBean.getRaportXml());

		verifyNoInteractions(komunikaty);
	}
}
