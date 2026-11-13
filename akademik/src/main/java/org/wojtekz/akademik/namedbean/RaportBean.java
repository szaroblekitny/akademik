package org.wojtekz.akademik.namedbean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.function.Consumer;

import jakarta.faces.application.FacesMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.service.NiekompletneDaneException;
import org.wojtekz.akademik.service.RaportService;

/**
 * Bean JSF do raportu kwaterunku: tworzy plik {@code raport-kwaterunku.xml}
 * (krok 2.4) albo {@code raport-kwaterunku.html} (krok 2.5) z aktualnym stanem
 * akademika i przekazuje go przeglądarce. Przyciski są na stronie głównej,
 * obok tabeli kwaterunku.
 * <p>
 * Dwa rodzaje błędów, obsługiwane różnie (zasada z kroku 2.6):
 * <ul>
 * <li>błąd w danych ({@link NiekompletneDaneException}, np. student bez płci):
 *     użytkownik może go poprawić, więc bean dodaje komunikat, a getter zwraca {@code null};
 *     wtedy {@code <p:fileDownload>} niczego nie wysyła, a strona odświeża się z komunikatem;</li>
 * <li>każdy inny, gruby błąd (np. brak połączenia z bazą) wychodzi z gettera
 *     i JSF przenosi użytkownika na stronę błędu {@code errors.xhtml}.</li>
 * </ul>
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

	/** Nazwa pliku raportu HTML (krok 2.5). */
	static final String NAZWA_PLIKU_HTML = "raport-kwaterunku.html";

	/** Tytuł komunikatów tego beana. */
	private static final String RAPORT = "Raport";

	/** {@code transient}: usługa Springa nie powinna być częścią serializowanej sesji. */
	private transient RaportService raportService;

	@Autowired
	public void setRaportService(RaportService raportService) {
		this.raportService = raportService;
	}

	/** Komunikaty JSF; {@code transient} jak w pozostałych beanach. */
	private transient Messagesy komunikaty;

	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}

	/**
	 * Tworzy raport XML z aktualnych danych. {@code <p:fileDownload>} woła tę metodę
	 * przy każdym kliknięciu przycisku i wysyła wynik do przeglądarki.
	 *
	 * @return zawartość pliku, nazwa i typ
	 */
	public StreamedContent getRaportXml() {
		return plik(NAZWA_PLIKU, "application/xml", raportService::zapiszRaport);
	}

	/**
	 * Tworzy raport HTML z aktualnych danych (krok 2.5), do obejrzenia w przeglądarce.
	 *
	 * @return zawartość pliku, nazwa i typ
	 */
	public StreamedContent getRaportHtml() {
		// kodowanie znaków podaje sama strona (<meta charset="UTF-8"/>); nie mylić
		// z contentEncoding, które oznacza kompresję (np. gzip), a nie znaki
		return plik(NAZWA_PLIKU_HTML, "text/html", raportService::zapiszRaportHtml);
	}

	/**
	 * Wspólna część obu raportów: zapis do pamięci (w transakcji usługi) i plik do pobrania.
	 * Obsługa błędów jest opisana w opisie klasy.
	 *
	 * @param nazwa nazwa pliku dla przeglądarki
	 * @param typ typ MIME pliku
	 * @param zapis metoda usługi zapisująca raport do strumienia
	 * @return plik albo {@code null} przy błędzie
	 */
	private StreamedContent plik(String nazwa, String typ, Consumer<OutputStream> zapis) {
		ByteArrayOutputStream bajty = new ByteArrayOutputStream();
		try {
			zapis.accept(bajty);
		} catch (NiekompletneDaneException nde) {
			// błąd w danych: użytkownik może go poprawić, więc pokazujemy treść
			logg.warn("Raport {} niemożliwy: {}", nazwa, nde.getMessage());
			komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, RAPORT, nde.getMessage(), null);
			return null;
		}
		// innych wyjątków celowo nie łapiemy: gruby błąd ma trafić na stronę błędu
		byte[] zawartosc = bajty.toByteArray();
		logg.debug("-------> RaportBean: {}, {} bajtów", nazwa, zawartosc.length);

		// lambda dostaje tylko tablicę bajtów, która jest serializowalna
		return DefaultStreamedContent.builder()
				.name(nazwa)
				.contentType(typ)
				.contentLength((long) zawartosc.length)
				.stream(() -> new ByteArrayInputStream(zawartosc))
				.build();
	}
}
