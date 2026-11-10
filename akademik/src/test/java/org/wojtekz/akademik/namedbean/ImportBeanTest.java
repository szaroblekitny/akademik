package org.wojtekz.akademik.namedbean;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.faces.application.FacesMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Test beana importu (krok 2.3 planu naprawczego). Przesłany plik udaje mock
 * {@link UploadedFile} z Mockito, a komunikaty JSF mock {@link Messagesy}, bo w teście
 * nie ma {@code FacesContext}. Usługa i baza HSQLDB są prawdziwe.
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
@WebAppConfiguration
class ImportBeanTest {

	@Autowired
	private ImportBean importBean;

	@Autowired
	private PokojRepository pokojRepo;

	@Autowired
	private StudentRepository studentRepo;

	private Messagesy komunikaty;

	@BeforeEach
	void przygotuj() {
		wyczyscBaze();
		komunikaty = mock(Messagesy.class);
		importBean.setMessagesy(komunikaty);
		// stary pokój, który import ma skasować
		Pokoj stary = new Pokoj();
		stary.setId(1);
		stary.setNumerPokoju("STARY");
		stary.setLiczbaMiejsc(1);
		pokojRepo.save(stary);
	}

	@AfterEach
	void wyczyscBaze() {
		studentRepo.deleteAll();
		pokojRepo.deleteAll();
	}

	/** Przykład z zasobów testowych zastępuje dane w bazie, a ekran dostaje wynik. */
	@Test
	void testImportuPliku() throws IOException {
		try (InputStream przyklad = getClass().getResourceAsStream("/dane/przyklad.xml")) {
			Assertions.assertNotNull(przyklad, "Brak pliku /dane/przyklad.xml w zasobach testowych");
			importBean.setFile(plik("przyklad.xml", przyklad.readAllBytes()));
		}

		importBean.upload();

		Assertions.assertEquals(4, pokojRepo.count(), "Powinny być tylko 4 pokoje z pliku");
		Assertions.assertTrue(pokojRepo.findByNumerPokoju("STARY").isEmpty(), "Stary pokój powinien zniknąć");
		Assertions.assertEquals(7, studentRepo.count());
		verify(komunikaty).addMessage(ImportBean.IMPORT_PLIKU, "Plik przyklad.xml: wczytano pokoi 4, studentów 7");
	}

	/** Błędny plik: komunikat o błędzie, a stare dane zostają. */
	@Test
	void testImportuBlednegoPliku() throws IOException {
		String xml = """
				<akademik xmlns="urn:wojtekz:akademik:dane:1">
				  <pokoje>
				    <pokoj numer="101" miejsca="0"/>
				  </pokoje>
				</akademik>
				""";
		importBean.setFile(plik("zly.xml", xml.getBytes(StandardCharsets.UTF_8)));

		importBean.upload();

		verify(komunikaty).addMessage(eq(FacesMessage.SEVERITY_ERROR), eq(ImportBean.IMPORT_PLIKU),
				contains("Nieprawidłowy plik"), isNull());
		Assertions.assertEquals(1, pokojRepo.count(), "Stary pokój powinien zostać");
		Assertions.assertFalse(pokojRepo.findByNumerPokoju("STARY").isEmpty());
	}

	@Test
	void testBrakuPliku() {
		importBean.setFile(null);

		importBean.upload();

		verify(komunikaty).addMessage(ImportBean.IMPORT_PLIKU, "Brak pliku");
		Assertions.assertEquals(1, pokojRepo.count());
	}

	// ----- pomocnicze -----

	/** Udawany plik przesłany przez {@code <p:fileUpload>}. */
	private static UploadedFile plik(String nazwa, byte[] zawartosc) throws IOException {
		UploadedFile plik = mock(UploadedFile.class);
		when(plik.getFileName()).thenReturn(nazwa);
		when(plik.getSize()).thenReturn((long) zawartosc.length);
		when(plik.getInputStream()).thenReturn(new ByteArrayInputStream(zawartosc));
		return plik;
	}
}
