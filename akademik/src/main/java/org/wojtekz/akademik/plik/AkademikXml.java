package org.wojtekz.akademik.plik;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Korzeń pliku XML dla całego akademika: {@code <akademik wersja="1">} z sekcjami {@code <pokoje>} i {@code <studenci>}.
 * <p>
 * {@code @XmlAccessorType(FIELD)}: JAXB czyta i zapisuje pola bezpośrednio, więc klasa
 * nie potrzebuje setterów. Potrzebuje za to konstruktora bezargumentowego, bo JAXB
 * tworzy obiekt pusty i dopiero potem wypełnia pola. Z tego powodu nie może to być
 * {@code record}: rekord ma tylko konstruktor ze wszystkimi polami.
 */
@XmlRootElement(name = "akademik")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"pokoje", "studenci"})
public class AkademikXml {

	/** Przestrzeń nazw z {@code akademik.xsd}. */
	public static final String PRZESTRZEN_NAZW = "urn:wojtekz:akademik:dane:1";

	/** Wersja formatu zapisywana w atrybucie {@code wersja}. */
	public static final String WERSJA = "1";

	@XmlAttribute
	private String wersja = WERSJA;

	// @XmlElementWrapper tworzy element-opakowanie <pokoje>, a @XmlElement nazywa
	// każdy element listy <pokoj>. Lista null = brak całej sekcji w pliku.
	@XmlElementWrapper(name = "pokoje")
	@XmlElement(name = "pokoj")
	private List<PokojXml> pokoje;

	@XmlElementWrapper(name = "studenci")
	@XmlElement(name = "student")
	private List<StudentXml> studenci;

	/** Dla JAXB. */
	public AkademikXml() {
	}

	public AkademikXml(List<PokojXml> pokoje, List<StudentXml> studenci) {
		this.pokoje = new ArrayList<>(pokoje);
		this.studenci = new ArrayList<>(studenci);
	}

	public String getWersja() {
		return wersja;
	}

	/** @return pokoje z pliku; pusta lista, gdy w pliku nie było sekcji {@code <pokoje>} */
	public List<PokojXml> getPokoje() {
		return pokoje == null ? List.of() : pokoje;
	}

	/** @return studenci z pliku; pusta lista, gdy w pliku nie było sekcji {@code <studenci>} */
	public List<StudentXml> getStudenci() {
		return studenci == null ? List.of() : studenci;
	}
}
