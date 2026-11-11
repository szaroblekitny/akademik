package org.wojtekz.akademik.raport;

import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Student;

/**
 * Student w raporcie: tylko to, co raport pokazuje, bez ID i bez pokoju.
 *
 * @param imie imię
 * @param nazwisko nazwisko
 * @param plec płeć
 */
public record StudentRaportu(String imie, String nazwisko, Plec plec) {

	/**
	 * Metoda statyczna w rekordzie: rekord może mieć metody fabryczne jak zwykła klasa.
	 *
	 * @param student encja z bazy
	 * @return student w raporcie
	 */
	public static StudentRaportu zEncji(Student student) {
		return new StudentRaportu(student.getImie(), student.getNazwisko(), student.getPlec());
	}
}
