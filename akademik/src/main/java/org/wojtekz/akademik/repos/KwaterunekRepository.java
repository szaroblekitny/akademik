package org.wojtekz.akademik.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Kwaterunek;

/**
 * Interfejs rozszerzający JpaRepository dla klasy Kwaterunek. Zawiera dodatkowe metody
 * dla obsługi Kwaterunku w bazie danych.
 * 
 * @author Wojtek Zaręba
 *
 */
public interface KwaterunekRepository extends JpaRepository<Kwaterunek, Long> {
	
	/**
	 * Wyszukuje rekordy klasy Kwaterunek dla danego identyfikatora studenta.
	 * Teoretycznie powinien być jeden taki rekord pokazujący, w którym pokoju
	 * mieszka student o podanym identyfikatorze.
	 * 
	 * @param idStudenta identyfikator studenta
	 * @return lista obiektów Kwaterunek
	 */
	@Query("select kw from Kwaterunek kw where kw.student = ?1")
	public List<Kwaterunek> findByIdStudenta(long idStudenta);
	
	/**
	 * Wyszukuje rekordy klasy Kwaterunek dla danego identyfikatora pokoju.
	 * Tworzy spis studentów mieszkających w podanym pokoju.
	 * 
	 * @param idPokoju idnetyfikator pokoju
	 * @return lista obiektów Kwaterunek o identycznym identyfikatorze pokoju
	 */
	@Query("select kw from Kwaterunek kw where kw.pokoj = ?1")
	public List<Kwaterunek> findByIdPokoju(long idPokoju);

}
