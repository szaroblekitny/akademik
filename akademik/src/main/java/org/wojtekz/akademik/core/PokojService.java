package org.wojtekz.akademik.core;

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
	public void save(Pokoj pokoj);
	public List<Pokoj> listAll();
	public Pokoj findByNumber(String numerPokoju);
	public long ilePokoi();
	public void deleteAll();
	public void deleteByNumber(String numerPokoju);

}
