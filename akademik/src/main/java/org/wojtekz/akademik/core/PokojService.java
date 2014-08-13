package org.wojtekz.akademik.core;

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
	public void save(Pokoj pokoj);
	public List<Pokoj> listAll();
	public Pokoj findByNumber(String numerPokoju);
	public long ilePokoi();
	public void deleteAll();
	public void deleteByNumber(String numerPokoju);

}
