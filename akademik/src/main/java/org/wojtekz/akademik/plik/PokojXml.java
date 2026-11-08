package org.wojtekz.akademik.plik;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import org.wojtekz.akademik.entity.Pokoj;

/**
 * Element reprezentujący pokój w pliku XML: {@code <pokoj numer="101" miejsca="2"/>}.
 * Bez ID i bez mieszkańców.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PokojXml {

	@XmlAttribute(required = true)
	private String numer;

	@XmlAttribute(required = true)
	private int miejsca;

	/** Dla JAXB. */
	public PokojXml() {
	}

	public PokojXml(String numer, int miejsca) {
		this.numer = numer;
		this.miejsca = miejsca;
	}

	/**
	 * @param pokoj encja z bazy
	 * @return pokój w formacie pliku
	 */
	public static PokojXml zEncji(Pokoj pokoj) {
		return new PokojXml(pokoj.getNumerPokoju(), pokoj.getLiczbaMiejsc());
	}

	/**
	 * @return nowa encja bez ID (ID nada aplikacja przy zapisie do bazy)
	 */
	public Pokoj naEncje() {
		Pokoj pokoj = new Pokoj();
		pokoj.setNumerPokoju(numer);
		pokoj.setLiczbaMiejsc(miejsca);
		return pokoj;
	}

	public String getNumer() {
		return numer;
	}

	public int getMiejsca() {
		return miejsca;
	}
}
