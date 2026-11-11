package org.wojtekz.akademik.service;

import java.io.OutputStream;
import java.text.Collator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.raport.PokojRaportu;
import org.wojtekz.akademik.raport.RaportKwaterunku;
import org.wojtekz.akademik.raport.StudentRaportu;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Usługa tworząca raport kwaterunku (krok 2.4 planu naprawczego): migawkę stanu
 * akademika z bazy i jej zapis do XML w formacie {@code /xsd/raport.xsd}.
 * <p>
 * Cała klasa ma {@code readOnly = true}: raport tylko czyta bazę.
 *
 * @author Wojciech Zaręba
 */
@Service
@Transactional(readOnly = true)
public class RaportService {
	private static final Logger logg = LogManager.getLogger();

	/** Przestrzeń nazw raportu, inna niż pliku danych. */
	public static final String PRZESTRZEN_NAZW = "urn:wojtekz:akademik:raport:1";

	/**
	 * Format daty dla {@code xs:dateTime}, zawsze z sekundami. {@code LocalDateTime.toString()}
	 * pomija sekundy, gdy są równe zero ({@code 2026-11-10T20:15}), a schemat by tego nie przyjął.
	 */
	private static final DateTimeFormatter FORMAT_DATY = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final PokojRepository pokojRepo;
	private final StudentRepository studentRepo;

	public RaportService(PokojRepository pokojRepo, StudentRepository studentRepo) {
		this.pokojRepo = pokojRepo;
		this.studentRepo = studentRepo;
	}

	/**
	 * Tworzy raport z aktualnego stanu bazy: pokoje w kolejności numerów,
	 * a studenci w kolejności nazwisk i imion (polska kolejność liter).
	 *
	 * @return raport kwaterunku
	 */
	public RaportKwaterunku utworzRaport() {
		// Collator porównuje napisy według zasad języka: „Ś” stoi po „S”, a nie za „Z”,
		// jak przy zwykłym compareTo, które porównuje kody znaków. Collator nie jest
		// bezpieczny wielowątkowo, więc tworzymy go przy każdym raporcie.
		Collator polski = Collator.getInstance(Locale.forLanguageTag("pl"));
		Comparator<StudentRaportu> poNazwisku = Comparator
				.comparing(StudentRaportu::nazwisko, polski)
				.thenComparing(StudentRaportu::imie, polski);

		// studenci według ID pokoju; bez pokoju osobno
		Map<Long, List<StudentRaportu>> mieszkancy = new HashMap<>();
		List<StudentRaportu> niezakwaterowani = new ArrayList<>();
		for (Student student : studentRepo.findAll()) {
			StudentRaportu wpis = StudentRaportu.zEncji(student);
			if (student.getPokoj() == null) {
				niezakwaterowani.add(wpis);
			} else {
				mieszkancy.computeIfAbsent(student.getPokoj().getId(), id -> new ArrayList<>()).add(wpis);
			}
		}
		niezakwaterowani.sort(poNazwisku);

		List<PokojRaportu> pokoje = new ArrayList<>();
		for (Pokoj pokoj : pokojRepo.findAll()) {
			List<StudentRaportu> wPokoju = mieszkancy.getOrDefault(pokoj.getId(), new ArrayList<>());
			wPokoju.sort(poNazwisku);
			pokoje.add(new PokojRaportu(pokoj.getNumerPokoju(), pokoj.getLiczbaMiejsc(), wPokoju));
		}
		pokoje.sort(Comparator.comparing(PokojRaportu::numer, polski));

		RaportKwaterunku raport = new RaportKwaterunku(
				LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), pokoje, niezakwaterowani);
		logg.debug("----->>> utworzRaport: zakwaterowanych {} z {}", raport.zakwaterowanych(), raport.studentow());
		return raport;
	}

	/**
	 * Tworzy raport i zapisuje go jako XML.
	 *
	 * @param out cel zapisu; metoda go nie zamyka
	 * @return zapisany raport (np. żeby pokazać podsumowanie na ekranie)
	 */
	public RaportKwaterunku zapiszRaport(OutputStream out) {
		// wywołanie z wnętrza klasy omija proxy, ale tu to nie szkodzi:
		// zapiszRaport sama jest już w transakcji tylko do odczytu
		RaportKwaterunku raport = utworzRaport();
		try {
			XMLStreamWriter xml = XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8");
			xml.writeStartDocument("UTF-8", "1.0");
			wiersz(xml, 0);
			// pierwszy element ustala domyślną przestrzeń nazw; kolejne ją dziedziczą
			xml.setDefaultNamespace(PRZESTRZEN_NAZW);
			xml.writeStartElement(PRZESTRZEN_NAZW, "raport");
			xml.writeDefaultNamespace(PRZESTRZEN_NAZW);
			xml.writeAttribute("wersja", "1");
			xml.writeAttribute("utworzono", FORMAT_DATY.format(raport.utworzono()));

			for (PokojRaportu pokoj : raport.pokoje()) {
				wiersz(xml, 1);
				xml.writeStartElement(PRZESTRZEN_NAZW, "pokoj");
				xml.writeAttribute("numer", pokoj.numer());
				xml.writeAttribute("miejsca", String.valueOf(pokoj.miejsca()));
				xml.writeAttribute("wolne", String.valueOf(pokoj.wolne()));
				zapiszStudentow(xml, pokoj.mieszkancy(), 2);
				if (!pokoj.mieszkancy().isEmpty()) {
					wiersz(xml, 1);
				}
				xml.writeEndElement();
			}

			if (!raport.niezakwaterowani().isEmpty()) {
				wiersz(xml, 1);
				xml.writeStartElement(PRZESTRZEN_NAZW, "niezakwaterowani");
				zapiszStudentow(xml, raport.niezakwaterowani(), 2);
				wiersz(xml, 1);
				xml.writeEndElement();
			}

			wiersz(xml, 1);
			xml.writeEmptyElement(PRZESTRZEN_NAZW, "podsumowanie");
			xml.writeAttribute("pokoi", String.valueOf(raport.pokoi()));
			xml.writeAttribute("miejsc", String.valueOf(raport.miejsc()));
			xml.writeAttribute("studentow", String.valueOf(raport.studentow()));
			xml.writeAttribute("zakwaterowanych", String.valueOf(raport.zakwaterowanych()));

			wiersz(xml, 0);
			xml.writeEndElement();
			xml.writeEndDocument();
			xml.flush();
			xml.close();
		} catch (XMLStreamException ee) {
			throw new IllegalStateException("Nie da się zapisać raportu kwaterunku", ee);
		}
		return raport;
	}

	/** Zapisuje studentów jako elementy {@code <student>}, każdy w osobnym wierszu. */
	private static void zapiszStudentow(XMLStreamWriter xml, List<StudentRaportu> studenci, int poziom)
			throws XMLStreamException {
		for (StudentRaportu student : studenci) {
			wiersz(xml, poziom);
			xml.writeStartElement(PRZESTRZEN_NAZW, "student");
			xml.writeAttribute("plec", student.plec().name());
			xml.writeStartElement(PRZESTRZEN_NAZW, "imie");
			xml.writeCharacters(student.imie());
			xml.writeEndElement();
			xml.writeStartElement(PRZESTRZEN_NAZW, "nazwisko");
			xml.writeCharacters(student.nazwisko());
			xml.writeEndElement();
			xml.writeEndElement();
		}
	}

	/**
	 * Nowy wiersz i wcięcie. {@code XMLStreamWriter} pisze wszystko w jednej linii,
	 * więc żeby raport dało się czytać w edytorze, wcięcia dokładamy sami.
	 */
	private static void wiersz(XMLStreamWriter xml, int poziom) throws XMLStreamException {
		xml.writeCharacters("\n" + "  ".repeat(poziom));
	}
}
