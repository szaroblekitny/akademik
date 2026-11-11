package org.wojtekz.akademik.raport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cały raport kwaterunku. Liczby z podsumowania nie są osobnymi polami,
 * tylko wynikają z list, więc nie mogą się z nimi rozjechać.
 *
 * @param utworzono kiedy raport powstał
 * @param pokoje pokoje w kolejności numerów
 * @param niezakwaterowani studenci bez pokoju, w kolejności nazwisk i imion
 */
public record RaportKwaterunku(LocalDateTime utworzono, List<PokojRaportu> pokoje,
		List<StudentRaportu> niezakwaterowani) {

	public RaportKwaterunku {
		pokoje = List.copyOf(pokoje);
		niezakwaterowani = List.copyOf(niezakwaterowani);
	}

	/** @return liczba pokoi */
	public int pokoi() {
		return pokoje.size();
	}

	/** @return liczba wszystkich miejsc */
	public int miejsc() {
		return pokoje.stream().mapToInt(PokojRaportu::miejsca).sum();
	}

	/** @return liczba studentów z przydzielonym pokojem */
	public int zakwaterowanych() {
		return pokoje.stream().mapToInt(pokoj -> pokoj.mieszkancy().size()).sum();
	}

	/** @return liczba wszystkich studentów */
	public int studentow() {
		return zakwaterowanych() + niezakwaterowani.size();
	}
}
