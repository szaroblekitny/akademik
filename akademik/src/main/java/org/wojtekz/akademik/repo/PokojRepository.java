package org.wojtekz.akademik.repo;

import java.util.List;

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
	 * Wyszukiwanie pokoju po jego numerze.
	 * Teoretycznie może być kilka pokoi o tym samym numerze, chociaż nie powinno,
	 * ale życie jest bogate.
	 * 
	 * @param numerPokoju numer pokoju, nie jest tożsamy z identyfikatorem, np. może być numer "103A"
	 * @return obiekt Pokoj
	 */
	List<Pokoj> findByNumerPokoju(String numerPokoju);
	
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
