package org.wojtekz.akademik.namedbean;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.Serializable;

import jakarta.faces.application.FacesMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.plik.NieprawidlowyPlikException;
import org.wojtekz.akademik.service.DaneService;
import org.wojtekz.akademik.service.LiczbaRekordow;

/**
 * Bean JSF do importu: przyjmuje plik {@code akademik.xml} przesłany z komputera
 * użytkownika i zastępuje nim dane w bazie. Stare pokoje i studenci są kasowani,
 * dlatego ekran przed importem prosi o potwierdzenie.
 *
 * @author Wojciech Zaręba
 */
@Component
@Scope("session")
public class ImportBean implements Serializable {
	private static final long serialVersionUID = 1L;
	static final String IMPORT_PLIKU = "Import pliku";

	private static Logger logg = LogManager.getLogger();

	private transient Messagesy komunikaty;

	/** {@code transient}: usługa Springa nie powinna być częścią serializowanej sesji. */
	private transient DaneService daneService;

	private transient UploadedFile upFile;

	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}

	@Autowired
	public void setDaneService(DaneService daneService) {
		this.daneService = daneService;
	}

	public UploadedFile getFile() {
		return upFile;
	}

	public void setFile(UploadedFile upFile) {
		this.upFile = upFile;
	}

	/**
	 * Wczytuje przesłany plik do bazy w miejsce dotychczasowych danych
	 * i dodaje komunikat JSF z wynikiem albo z opisem błędu.
	 */
	public void upload() {
		logg.debug("-------> ImportBean upload");
		// bez wybranego pliku PrimeFaces może dać null albo pusty plik o rozmiarze 0
		if (upFile == null || upFile.getSize() == 0) {
			komunikaty.addMessage(IMPORT_PLIKU, "Brak pliku");
			return;
		}

		String nazwa = upFile.getFileName();
		// try-with-resources (Java 7): strumień zamknie się sam, także przy wyjątku
		try (InputStream plik = upFile.getInputStream()) {
			LiczbaRekordow wynik = daneService.importuj(plik);
			komunikaty.addMessage(IMPORT_PLIKU,
					"Plik " + nazwa + ": wczytano pokoi " + wynik.pokoje() + ", studentów " + wynik.studenci());
		} catch (NieprawidlowyPlikException npe) {
			// błąd po stronie pliku: użytkownik może go poprawić, więc pokazujemy szczegóły
			logg.warn("Nieprawidłowy plik {}: {}", nazwa, npe.getMessage());
			komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, IMPORT_PLIKU, npe.getMessage(), null);
		} catch (IOException ee) {
			// Gruby błąd (np. przerwany odczyt przesłanego pliku) ma trafić na stronę błędu,
			// tak jak błąd bazy, którego tu w ogóle nie łapiemy. IOException jest kontrolowany,
			// więc opakowujemy go w niekontrolowany UncheckedIOException (Java 8).
			throw new UncheckedIOException("Nie da się odczytać przesłanego pliku " + nazwa, ee);
		}
	}
}
