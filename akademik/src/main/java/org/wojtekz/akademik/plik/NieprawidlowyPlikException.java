package org.wojtekz.akademik.plik;

/**
 * Plik z danymi nie pasuje do formatu: nie jest poprawnym XML, nie spełnia schematu
 * {@code akademik.xsd} albo zawiera niedozwoloną deklarację DTD (Document Type Definition).
 * <p>
 * Wyjątek niekontrolowany (dziedziczy po {@code RuntimeException}): rzucony wewnątrz
 * metody z {@code @Transactional} wycofuje transakcję, a ekran może pokazać
 * użytkownikowi {@link #getMessage()}.
 */
public class NieprawidlowyPlikException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NieprawidlowyPlikException(String komunikat, Throwable przyczyna) {
		super(komunikat, przyczyna);
	}
}
