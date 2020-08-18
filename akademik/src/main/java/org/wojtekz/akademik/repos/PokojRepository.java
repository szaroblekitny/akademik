package org.wojtekz.akademik.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Pokoj;

/**
 * Interfejs rozszerzający JpaRepository dla klasy Pokoj. Zawiera dodatkowe metody
 * dla obsługi Pokoi w bazie danych.
 * 
 * @author Wojtek Zaręba
 *
 */
public interface PokojRepository extends JpaRepository<Pokoj, Long> {
	
	/**
	 * Wyszukiwanie pokoju po jego numerze. Tu jest niejawne założenie, że istnieje
	 * tylko jeden pokój o podanym numerze.
	 * 
	 * @param numerPokoju numer pokoju, nie jest tożsamy z identyfikatorem, np. może być numer "103A"
	 * @return obiekt Pokoj
	 */
	@Query("select pp from Pokoj pp where pp.numerPokoju = ?1")
	Pokoj findByNumber(String numerPokoju);
	
	/**
	 * Kasowanie pokoju o podanym numerze. Ponieważ rekordy są zmieniane,
	 * konieczne jest dodanie adnotacji Modifying.
	 * 
	 * @param numerPokoju numer pokoju
	 */
	@Modifying
	@Query("delete from Pokoj p where p.numerPokoju = ?1")
	void deleteByNumber(String numerPokoju);

}
