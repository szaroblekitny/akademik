package org.wojtekz.akademik.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Konwertuje enumerator {@link Plec} na napis zapisywany w bazie danych
 * ("Kobieta" lub "Mezczyzna") i odwrotnie - z bazy na wartość enumeratora.
 * 
 * @author Wojciech Zareba
 *
 */
@Converter
public class KonwerterPlci implements AttributeConverter<Plec, String> {

	@Override
	public String convertToDatabaseColumn(Plec attribute) {
		if ( attribute == null ) {
			return null;
		}

		return attribute.getCode();
	}

	@Override
	public Plec convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}

		return Plec.fromCode(dbData);
	}

}
