package org.wojtekz.akademik.namedbean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

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
}
