package org.wojtekz.akademik.service;

import java.util.List;

import org.wojtekz.akademik.entity.Student;

/**
 * Dane w bazie są niekompletne i nie da się z nich zrobić eksportu ani raportu,
 * np. student nie ma określonej płci, a schematy plików jej wymagają.
 * <p>
 * Komunikat jest przeznaczony dla użytkownika: mówi, co trzeba poprawić.
 * Wyjątek jest niekontrolowany ({@code RuntimeException}), tak jak
 * {@code NieprawidlowyPlikException}.
 *
 * @author Wojciech Zaręba
 */
public class NiekompletneDaneException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NiekompletneDaneException(String komunikat) {
		super(komunikat);
	}

	/**
	 * Sprawdza, czy wszyscy studenci mają płeć, której wymagają schematy pliku danych
	 * i raportu. Wspólne dla eksportu i raportu, żeby komunikat był ten sam.
	 *
	 * @param bezPlci studenci z pustą płcią (z {@code findByPlecIsNull()})
	 * @throws NiekompletneDaneException gdy lista nie jest pusta
	 */
	public static void sprawdzPlec(List<Student> bezPlci) {
		if (!bezPlci.isEmpty()) {
			String kto = String.join(", ", bezPlci.stream()
					.map(student -> student.getImie() + " " + student.getNazwisko())
					.toList());
			throw new NiekompletneDaneException(
					"Brak określonej płci: " + kto + ". Popraw dane na ekranie Studenci.");
		}
	}
}
