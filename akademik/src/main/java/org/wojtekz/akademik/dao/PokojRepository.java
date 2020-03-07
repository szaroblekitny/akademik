package org.wojtekz.akademik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
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
	public Pokoj findByNumber(String numerPokoju);
	
	/**
	 * Kasowanie pokoju o podanym numerze.
	 * 
	 * @param numerPokoju numer pokoju
	 */
	@Query("delete Pokoj pp where pp.numerPokoju = ?1")
	public void deleteByNumber(String numerPokoju);

}
