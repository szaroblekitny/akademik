package org.wojtekz.akademik.dao;

import java.util.List;

import org.wojtekz.akademik.entity.Pokoj;

/**
 * <p>Ten interfejs specyfikuje metody niezbêdne do przetwarzania klasy (tabeli)
 * Pokoj w bazie danych. Zwykle zawiera metody wyszukiwania, zapisu, odczytu
 * i modyfikacji (CRUD). Uniezale¿nia core aplikacji od warstwy bezpoœrednio
 * siêgaj¹cej do bazy danych (w tym przypadku zrealizowanej na repozytoriach
 * Spring Data JPA). Zapewne mo¿na to jeszcze jakoœ uogólniæ...</p>
 * 
 * <p>W naszym przypadku chcemy zapsiaæ pokój w bazie danych, odzczytaæ
 * z bazy - zarówno pojedynczy pokój, jak i listê, znaæ liczbê pokoi, zmianê
 * danych zostawiê na póŸniej.</p>
 * 
 * 
 * @author Wojtek Zarêba
 *
 */
public interface PokojService {
	/**
	 * Zapisuje pokój w bazie danych.
	 * @param pokoj Pokoj
	 */
	public void save(Pokoj pokoj);
	
	/**
	 * Pobiera wszystkie pokoje z bazy danych.
	 * @return lista pokoi
	 */
	public List<Pokoj> listAll();
	
	/**
	 * Wyszukuje w bazie pokój o podanym numerze.
	 * 
	 * @param numerPokoju jako String
	 * @return Pokoj
	 */
	public Pokoj findByNumber(String numerPokoju);
	
	/**
	 * Podaje liczbê pokoi zapisanych z bazie danych.
	 * 
	 * @return liczba pokoi
	 */
	public long ilePokoi();
	
	/**
	 * Kasuje ca³¹ tabelê pokoi.
	 */
	public void deleteAll();
	
	/**
	 * Kasuje pokój o wybranym numerze.
	 * 
	 * @param numerPokoju jako String (to nie jest id pokoju)
	 */
	public void deleteByNumber(String numerPokoju);

}
