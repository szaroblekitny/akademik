package org.wojtekz.akademik.service;

/**
 * Wynik importu: ile pokoi i ilu studentów zapisano w bazie.
 * <p>
 * To jest {@code record} (Java 16): niezmienna klasa danych. Kompilator sam tworzy
 * konstruktor {@code WynikImportu(int pokoje, int studenci)}, metody dostępu
 * {@code pokoje()} i {@code studenci()} (bez przedrostka "get") oraz {@code equals},
 * {@code hashCode} i {@code toString}. Dzięki {@code equals} test może porównać
 * cały wynik jednym {@code assertEquals}.
 *
 * @param pokoje liczba zapisanych pokoi
 * @param studenci liczba zapisanych studentów
 */
public record WynikImportu(int pokoje, int studenci) {

	/**
	 * Zwykła metoda w rekordzie: rekord może mieć własne metody, byle nie zmieniały pól.
	 *
	 * @return łączna liczba zapisanych rekordów
	 */
	public int razem() {
		return pokoje + studenci;
	}
}
