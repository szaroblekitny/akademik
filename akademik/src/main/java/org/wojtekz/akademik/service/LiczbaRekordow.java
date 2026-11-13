package org.wojtekz.akademik.service;

/**
 * Wynik importu albo eksportu: ile pokoi i ilu studentów przeszło między plikiem a bazą.
 * <p>
 * To jest {@code record} (Java 16): niezmienna klasa danych. Kompilator sam tworzy
 * konstruktor {@code LiczbaRekordow(int pokoje, int studenci)}, metody dostępu
 * {@code pokoje()} i {@code studenci()} (bez przedrostka "get") oraz {@code equals},
 * {@code hashCode} i {@code toString}. Dzięki {@code equals} test może porównać
 * cały wynik jednym {@code assertEquals}.
 * <p>
 * Do kroku 2.1a nazywał się {@code WynikImportu}. Nazwa się zmieniła, bo ten sam
 * wynik zwraca teraz także eksport.
 *
 * @param pokoje liczba pokoi
 * @param studenci liczba studentów
 */
public record LiczbaRekordow(int pokoje, int studenci) {

	/**
	 * Zwykła metoda w rekordzie: rekord może mieć własne metody, byle nie zmieniały pól.
	 *
	 * @return łączna liczba rekordów
	 */
	public int razem() {
		return pokoje + studenci;
	}
}
