package org.wojtekz.akademik.dao;

import java.util.List;

import org.wojtekz.akademik.entity.Pokoj;

/**
 * <p>Ten interfejs specyfikuje metody niezb�dne do przetwarzania klasy (tabeli)
 * Pokoj w bazie danych. Zwykle zawiera metody wyszukiwania, zapisu, odczytu
 * i modyfikacji (CRUD). Uniezale�nia core aplikacji od warstwy bezpo�rednio
 * si�gaj�cej do bazy danych (w tym przypadku zrealizowanej na repozytoriach
 * Spring Data JPA). Zapewne mo�na to jeszcze jako� uog�lni�...</p>
 * 
 * <p>W naszym przypadku chcemy zapsia� pok�j w bazie danych, odzczyta�
 * z bazy - zar�wno pojedynczy pok�j, jak i list�, zna� liczb� pokoi, zmian�
 * danych zostawi� na p�niej.</p>
 * 
 * 
 * @author Wojtek Zar�ba
 *
 */
public interface PokojService {
	/**
	 * Zapisuje pok�j w bazie danych.
	 * @param pokoj Pokoj
	 */
	public void save(Pokoj pokoj);
	
	/**
	 * Pobiera wszystkie pokoje z bazy danych.
	 * @return lista pokoi
	 */
	public List<Pokoj> listAll();
	
	/**
	 * Wyszukuje w bazie pok�j o podanym numerze.
	 * 
	 * @param numerPokoju jako String
	 * @return Pokoj
	 */
	public Pokoj findByNumber(String numerPokoju);
	
	/**
	 * Podaje liczb� pokoi zapisanych z bazie danych.
	 * 
	 * @return liczba pokoi
	 */
	public long ilePokoi();
	
	/**
	 * Kasuje ca�� tabel� pokoi.
	 */
	public void deleteAll();
	
	/**
	 * Kasuje pok�j o wybranym numerze.
	 * 
	 * @param numerPokoju jako String (to nie jest id pokoju)
	 */
	public void deleteByNumber(String numerPokoju);

}
