package org.wojtekz.akademik.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wojtekz.akademik.entity.Kwaterunek;

/**
 * Interfejs rozszerzaj¹cy JpaRepository dla klasy Kwaterunek. Zawiera dodatkowe metody
 * dla obs³ugi Kwaterunku w bazie danych.
 * 
 * @author Wojtek Zarêba
 *
 */
@Repository
public interface KwaterunekRepository extends JpaRepository<Kwaterunek, Long> {
	
	/**
	 * Wyszukuje rekordy klasy Kwaterunek dla danego identyfikatora studenta.
	 * Teoretycznie powinien byæ jeden taki rekord pokazuj¹cy, w którym pokoju
	 * mieszka student o podanym identyfikatorze.
	 * 
	 * @param idStudenta identyfikator studenta
	 * @return lista obiektów Kwaterunek
	 */
	@Query("select kw from Kwaterunek kw where kw.student = ?1")
	public List<Kwaterunek> findByIdStudenta(long idStudenta);
	
	/**
	 * Wyszukuje rekordy klasy Kwaterunek dla danego identyfikatora pokoju.
	 * Tworzy spis studentów mieszkaj¹cych w podanym pokoju.
	 * 
	 * @param idPokoju idnetyfikator pokoju
	 * @return lista obiektów Kwaterunek o identycznym identyfikatorze pokoju
	 */
	@Query("select kw from Kwaterunek kw where kw.pokoj = ?1")
	public List<Kwaterunek> findByIdPokoju(long idPokoju);

}
