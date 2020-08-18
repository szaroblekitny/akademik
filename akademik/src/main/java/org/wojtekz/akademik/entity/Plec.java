package org.wojtekz.akademik.entity;

/**
 * Wbrew wszelkim odchyłom płcie są tylko dwie: kobieta i mężczyzna.
 * Enumeracja jest pokrętna, bo w bazie trzymamy napisy "Kobieta" lub "Mezczyzna".
 * 
 * @author Wojtek Zaręba
 *
 */
public enum Plec {
	KOBIETA("Kobieta"),
	MEZCZYZNA("Mezczyzna");
	
	private final String code;

	Plec(String code) {
        this.code = code;
    }

    public static Plec fromCode(String code) {
        if ( "Mezczyzna".equals(code) ) {
            return MEZCZYZNA;
        }
        if ( "Kobieta".equals(code) ) {
            return KOBIETA;
        }
        throw new UnsupportedOperationException("----->> Takiej pci brak");
    }

    public String getCode() {
        return code;
    }
}
