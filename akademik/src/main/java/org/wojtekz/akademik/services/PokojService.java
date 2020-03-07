package org.wojtekz.akademik.services;

import java.util.List;

import org.wojtekz.akademik.entity.Pokoj;

/**
 * <p>Ten interfejs specyfikuje metody niezbędne do przetwarzania klasy (tabeli)
 * Pokoj w bazie danych. Zwykle zawiera metody wyszukiwania, zapisu, odczytu
 * i modyfikacji (CRUD). Uniezależnia core aplikacji od warstwy bezpośrednio
 * sięgającej do bazy danych (w tym przypadku zrealizowanej na repozytoriach
 * Spring Data JPA). Zapewne można to jeszcze jakoś uogólnić...</p>
 * 
 * <p>W naszym przypadku chcemy zapsiać pokój w bazie danych, odzczytać
 * z bazy - zarówno pojedynczy pokój, jak i listę, znać liczbę pokoi, zmianę
 * danych zostawię na później.</p>
 * 
 * 
 * @author Wojtek Zaręba
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
	 * Podaje liczbę pokoi zapisanych z bazie danych.
	 * 
	 * @return liczba pokoi
	 */
	public long ilePokoi();
	
	/**
	 * Kasuje całą tabelę pokoi.
	 */
	public void deleteAll();
	
	/**
	 * Kasuje pokój o wybranym numerze.
	 * 
	 * @param numerPokoju jako String (to nie jest id pokoju)
	 */
	public void deleteByNumber(String numerPokoju);

}
