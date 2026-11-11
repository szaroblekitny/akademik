package org.wojtekz.akademik.namedbean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.raport.RaportKwaterunku;
import org.wojtekz.akademik.service.RaportService;

/**
 * Bean JSF do raportu kwaterunku: tworzy plik {@code raport-kwaterunku.xml}
 * z aktualnym stanem akademika i przekazuje go przeglądarce. Przycisk jest
 * na stronie głównej, obok tabeli kwaterunku.
 *
 * @author Wojciech Zaręba
 */
@Component
@Scope("session")
public class RaportBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logg = LogManager.getLogger();

	/** Nazwa pliku, którą przeglądarka zaproponuje przy zapisie. */
	static final String NAZWA_PLIKU = "raport-kwaterunku.xml";

	/** {@code transient}: usługa Springa nie powinna być częścią serializowanej sesji. */
	private transient RaportService raportService;

	@Autowired
	public void setRaportService(RaportService raportService) {
		this.raportService = raportService;
	}

	/**
	 * Tworzy raport XML z aktualnych danych. {@code <p:fileDownload>} woła tę metodę
	 * przy każdym kliknięciu przycisku i wysyła wynik do przeglądarki.
	 *
	 * @return zawartość pliku, nazwa i typ
	 */
	public StreamedContent getRaportXml() {
		// tak samo jak w EksportBean: najpierw cały raport do pamięci, w transakcji usługi
		ByteArrayOutputStream bajty = new ByteArrayOutputStream();
		RaportKwaterunku raport = raportService.zapiszRaport(bajty);
		byte[] plik = bajty.toByteArray();
		logg.debug("-------> RaportBean: zakwaterowanych {} z {}, {} bajtów",
				raport.zakwaterowanych(), raport.studentow(), plik.length);

		return DefaultStreamedContent.builder()
				.name(NAZWA_PLIKU)
				.contentType("application/xml")
				.contentLength((long) plik.length)
				.stream(() -> new ByteArrayInputStream(plik))
				.build();
	}
}
