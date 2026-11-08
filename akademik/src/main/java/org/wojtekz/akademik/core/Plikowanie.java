package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Plikowalny;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.plik.AkademikXml;
import org.wojtekz.akademik.plik.DaneZPliku;
import org.wojtekz.akademik.plik.NieprawidlowyPlikException;
import org.wojtekz.akademik.plik.PokojXml;
import org.wojtekz.akademik.plik.StudentXml;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

/**
 * Klasa do obsługi operacji plikowych.
 * 
 * @author Wojtek Zaręba
 *
 */
@Component
public class Plikowanie {
	private static Logger logg = LogManager.getLogger();

	/** Schemat pliku z danymi, w zasobach aplikacji. */
	private static final String SCHEMAT_XSD = "/xsd/akademik.xsd";

	/**
	 * Kontekst JAXB: opis klas z pakietu {@code plik} przygotowany raz, przy starcie.
	 * Jest bezpieczny wielowątkowo, ale tworzone z niego {@code Marshaller}
	 * i {@code Unmarshaller} już nie, więc te robimy przy każdym wywołaniu.
	 */
	private final JAXBContext kontekstJaxb;

	/** Skompilowany schemat {@code akademik.xsd}; też bezpieczny wielowątkowo. */
	private final Schema schemat;

	/**
	 * Przygotowuje JAXB i schemat. Błąd tutaj oznacza błąd w samej aplikacji
	 * (np. brak pliku XSD w WAR-ze), więc aplikacja nie powinna wystartować.
	 */
	public Plikowanie() {
		try {
			kontekstJaxb = JAXBContext.newInstance(AkademikXml.class);
			SchemaFactory fabryka = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemat = fabryka.newSchema(Plikowanie.class.getResource(SCHEMAT_XSD));
		} catch (JAXBException | SAXException ee) {
			throw new IllegalStateException("Nie da się przygotować JAXB albo schematu " + SCHEMAT_XSD, ee);
		}
	}

	@Autowired
	private Marshaller marshaller;
	
	@Autowired
	private Unmarshaller unmarshaller;
	
	/**
	 * Zapisuje listę obiektów do pliku XML.
	 * 
	 * @param writer BufferedWriter
	 * @param list lista obiektów podanego typu
	 * @throws XmlMappingException javax.naming.NamingException
	 * @throws IOException błąd wejścia-wyjścia
	 */
	public <T> void saveObjectList(BufferedWriter writer, List<T> list) throws IOException {
		logg.trace("----->>> saveObjectList method fired");
		
		if (writer == null) {
			logg.error("----->>> saveObjectList writer pusty");
			throw new IOException();
		}
		
		if (list == null || list.isEmpty()) {
			logg.error("----->>> saveObjectList pusta lista");
			throw new IOException();
		}
		
		StreamResult result = new StreamResult(writer);
		marshaller.marshal(list, result);
		
		logg.trace("----->>> saveObjectList koniec");
	}

	
	/**
	 * Przekształca dane z pliku XML na listę obiektów.
	 * 
	 * @param reader BufferedReader - odczytywane dane
	 * @return listę obiektów odczytanych z pliku
	 * @throws XmlMappingException w przypadku niepowodzenia mapowania pliku XML na obiekty
	 *         podanego typu; również gdy plik nie jest plikiem XML
	 * @throws IOException błąd odczytu z pliku
	 */
	@SuppressWarnings("unchecked")
	public List<Plikowalny> loadObjectList(BufferedReader reader)
			throws IOException {
		List<Plikowalny> obj;
		obj =  (List<Plikowalny>) unmarshaller.unmarshal(new StreamSource(reader));
		return obj;
	}

	// ===== JAXB (krok 2.1a, część 1): nowy format pliku <akademik> =====

	/**
	 * Zapisuje pokoje i studentów do jednego pliku XML w formacie {@code akademik.xsd}.
	 * Zapis też jest sprawdzany schematem, więc do pliku nie trafią dane, których
	 * import by potem nie przyjął (np. numer pokoju dłuższy niż 5 znaków).
	 *
	 * @param out cel zapisu; metoda go nie zamyka
	 * @param pokoje pokoje do zapisu
	 * @param studenci studenci do zapisu
	 * @throws NieprawidlowyPlikException gdy dane nie spełniają schematu albo zapis się nie udał
	 */
	public void zapiszDane(OutputStream out, List<Pokoj> pokoje, List<Student> studenci) {
		AkademikXml dokument = new AkademikXml(
				pokoje.stream().map(PokojXml::zEncji).toList(),
				studenci.stream().map(StudentXml::zEncji).toList());
		try {
			// pełna nazwa, bo w tej klasie Marshaller to jeszcze interfejs Springa dla XStreama
			jakarta.xml.bind.Marshaller zapisywacz = kontekstJaxb.createMarshaller();
			zapisywacz.setProperty(jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			zapisywacz.setProperty(jakarta.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
			zapisywacz.setSchema(schemat);
			zapisywacz.marshal(dokument, out);
		} catch (JAXBException ee) {
			throw new NieprawidlowyPlikException("Nie da się zapisać danych: " + opisBledu(ee), ee);
		}
		logg.debug("----->>> zapiszDane: {} pokoi, {} studentów", pokoje.size(), studenci.size());
	}

	/**
	 * Wczytuje plik XML w formacie {@code akademik.xsd} i sprawdza go schematem.
	 * <p>
	 * Parser StAX (Streaming API for XML) ma wyłączone DTD i encje zewnętrzne:
	 * plik z deklaracją {@code <!DOCTYPE ...>} zawierającą {@code <!ENTITY ... SYSTEM ...>}
	 * nie sięgnie po żaden plik ani adres (ochrona przed atakiem XXE, XML External Entity).
	 *
	 * @param in źródło danych; metoda go nie zamyka
	 * @return pokoje i studenci z pliku jako nowe encje, bez ID
	 * @throws NieprawidlowyPlikException gdy plik nie jest poprawnym XML albo nie spełnia schematu
	 */
	public DaneZPliku wczytajDane(InputStream in) {
		XMLInputFactory fabryka = XMLInputFactory.newFactory();
		fabryka.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		fabryka.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

		AkademikXml dokument;
		XMLStreamReader czytnik = null;
		try {
			czytnik = fabryka.createXMLStreamReader(in);
			jakarta.xml.bind.Unmarshaller odczytywacz = kontekstJaxb.createUnmarshaller();
			odczytywacz.setSchema(schemat);
			dokument = (AkademikXml) odczytywacz.unmarshal(czytnik);
		} catch (JAXBException ee) {
			throw new NieprawidlowyPlikException("Nieprawidłowy plik z danymi: " + opisBledu(ee), ee);
		} catch (XMLStreamException ee) {
			throw new NieprawidlowyPlikException("Nieprawidłowy plik z danymi: " + ee.getMessage(), ee);
		} finally {
			zamknij(czytnik);
		}

		DaneZPliku dane = new DaneZPliku(
				dokument.getPokoje().stream().map(PokojXml::naEncje).toList(),
				dokument.getStudenci().stream().map(StudentXml::naEncje).toList());
		logg.debug("----->>> wczytajDane: {} pokoi, {} studentów", dane.pokoje().size(), dane.studenci().size());
		return dane;
	}

	/**
	 * JAXB pakuje właściwą przyczynę (błąd parsera albo walidacji) w „linked exception”.
	 * Wyciągamy ją, żeby komunikat mówił, co i w którym wierszu jest nie tak.
	 */
	private static String opisBledu(JAXBException ee) {
		Throwable przyczyna = ee.getLinkedException() != null ? ee.getLinkedException() : ee;
		// instanceof z wzorcem (Java 16): sprawdzenie typu i zmienna sp w jednym kroku
		if (przyczyna instanceof SAXParseException sp) {
			return "wiersz " + sp.getLineNumber() + ": " + sp.getMessage();
		}
		return przyczyna.getMessage() != null ? przyczyna.getMessage() : przyczyna.toString();
	}

	private static void zamknij(XMLStreamReader czytnik) {
		if (czytnik != null) {
			try {
				czytnik.close();
			} catch (XMLStreamException ee) {
				logg.warn("----->>> nie udało się zamknąć czytnika XML", ee);
			}
		}
	}
}
