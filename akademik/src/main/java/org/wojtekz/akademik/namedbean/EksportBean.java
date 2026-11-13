package org.wojtekz.akademik.namedbean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import jakarta.faces.application.FacesMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.service.DaneService;
import org.wojtekz.akademik.service.LiczbaRekordow;
import org.wojtekz.akademik.service.NiekompletneDaneException;

/**
 * Bean JSF do eksportu danych: tworzy plik {@code akademik.xml} z pokojami
 * i studentami z bazy (format {@code akademik.xsd}) i przekazuje go przeglądarce,
 * która zapisuje go na komputerze użytkownika. Taki plik można potem
 * zaimportować z powrotem.
 *
 * @author Wojciech Zaręba
 */
@Component
@Scope("session")
public class EksportBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logg = LogManager.getLogger();

	/** Nazwa pliku, którą przeglądarka zaproponuje przy zapisie. */
	static final String NAZWA_PLIKU = "akademik.xml";

	/** Typ MIME (Multipurpose Internet Mail Extensions): mówi przeglądarce, co dostaje. */
	static final String TYP_PLIKU = "application/xml";

	/** Tytuł komunikatów tego beana. */
	private static final String EKSPORT = "Eksport";

	/**
	 * {@code transient}: bean sesyjny może zostać zserializowany (np. przy restarcie
	 * Tomcata), a usługa Springa nie jest serializowalna i nie powinna być częścią sesji.
	 */
	private transient DaneService daneService;

	@Autowired
	public void setDaneService(DaneService daneService) {
		this.daneService = daneService;
	}

	/** Komunikaty JSF; {@code transient} jak w pozostałych beanach. */
	private transient Messagesy komunikaty;

	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}

	/**
	 * Tworzy plik z aktualnymi danymi z bazy. {@code <p:fileDownload>} woła tę metodę
	 * przy każdym kliknięciu przycisku eksportu i wysyła wynik do przeglądarki.
	 *
	 * @return zawartość pliku, nazwa i typ
	 */
	public StreamedContent getContent() {
		// eksport kończy się tutaj, w transakcji usługi; potem mamy już tylko bajty
		ByteArrayOutputStream bajty = new ByteArrayOutputStream();
		LiczbaRekordow wynik;
		try {
			wynik = daneService.eksportuj(bajty);
		} catch (NiekompletneDaneException nde) {
			// błąd w danych zamiast strony błędu: komunikat i brak pliku (null),
			// wtedy <p:fileDownload> niczego nie wysyła
			logg.warn("Eksport niemożliwy: {}", nde.getMessage());
			komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, EKSPORT, nde.getMessage(), null);
			return null;
		}
		// innych wyjątków celowo nie łapiemy: gruby błąd (np. brak połączenia z bazą)
		// ma wyjść z beana, a JSF przeniesie wtedy użytkownika na stronę errors.xhtml
		byte[] plik = bajty.toByteArray();
		logg.debug("-------> EksportBean: {}, {} bajtów", wynik, plik.length);

		// lambda dostaje tylko tablicę bajtów (tablica jest serializowalna, a PrimeFaces
		// tego wymaga); każde wywołanie daje nowy strumień od początku pliku
		return DefaultStreamedContent.builder()
				.name(NAZWA_PLIKU)
				.contentType(TYP_PLIKU)
				.contentLength((long) plik.length)
				.stream(() -> new ByteArrayInputStream(plik))
				.build();
	}
}
