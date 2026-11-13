package org.wojtekz.akademik.raport;

import java.util.List;

/**
 * Pokój w raporcie razem z mieszkańcami.
 *
 * @param numer numer pokoju, np. "101"
 * @param miejsca liczba miejsc
 * @param mieszkancy zakwaterowani studenci, w kolejności nazwisk i imion
 */
public record PokojRaportu(String numer, int miejsca, List<StudentRaportu> mieszkancy) {

	/**
	 * Konstruktor kompaktowy (compact constructor): rekord może sprawdzić albo
	 * poprawić argumenty, zanim trafią do pól. Tutaj robimy niezmienną kopię listy,
	 * żeby nikt nie zmienił mieszkańców pokoju już po utworzeniu raportu.
	 */
	public PokojRaportu {
		mieszkancy = List.copyOf(mieszkancy);
	}

	/**
	 * @return liczba wolnych miejsc
	 */
	public int wolne() {
		return miejsca - mieszkancy.size();
	}
}
