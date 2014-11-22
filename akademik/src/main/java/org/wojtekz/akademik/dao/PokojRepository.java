package org.wojtekz.akademik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Pokoj;

/**
 * Interfejs rozszerzaj¹cy JpaRepository. Zawiera dodatkowe metody
 * dla obs³ugi Pokoi w bazie danych.
 * 
 * @author Wojtek Zarêba
 *
 */
public interface PokojRepository extends JpaRepository<Pokoj, Long> {
	
	/**
	 * Wyszukiwanie pokoju po jego numerze. Tu jest niejawne za³o¿enie, ¿e istnieje
	 * tylko jeden pokój o podanym numerze.
	 * 
	 * @param numerPokoju numer pokoju, nie jest to¿samy z identyfikatorem, np. mo¿e byæ numer "103A"
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
