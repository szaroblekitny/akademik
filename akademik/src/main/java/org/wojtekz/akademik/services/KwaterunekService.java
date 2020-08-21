package org.wojtekz.akademik.services;

import java.util.List;

import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Student;


/**
 * Serwis dla klasy (tabeli) Kwaterunek. Zawiera metody obsługi rekordów dla tej tabeli.
 * 
 * @author Wojtek
 *
 */
public interface KwaterunekService {
	
	/**
	 * Zapisuje obiekt Kwaterunek do bazy.
	 * @param kwaterunek zapisywany obiekt
	 */
	void save(Kwaterunek kwaterunek);
	
	/**
	 * Lista wszystkich kwaterunków w bazie.
	 * @return lista obiektów Kwaterunek
	 */
	List<Kwaterunek> listAll();
	
	/**
	 * Znajduje Kwaterunek po identyfikatorze własnym.
	 * 
	 * @param id identyfikator
	 * @return obiekt Kwaterunek
	 */
	Kwaterunek findById(long id);
	
	/**
	 * Znajduje Kwaterunek po identyfikatorze studenta. Teoretycznie powinien być to jeden
	 * rekord (?).
	 * 
	 * @param idStudenta identyfikator studenta przypisanego do powiązanego pokoju
	 * @return lista obiektów Kwaterunek
	 */
	List<Kwaterunek> findByIdStudenta(long idStudenta);
	
	/**
	 * Lista obiektów Kwaterunek o podanym identyfikatorze pokoju.
	 * 
	 * @param idPokoju identyfikator pokoju, w którym kwateruje powiązany student
	 * @return lista obiektów Kwaterunek
	 */
	List<Kwaterunek> findByIdPokoju(long idPokoju);
	
	/**
	 * Wyszukuje studentów zamieszkałych w pokoju przez wyszukanie ich kwaterunków.
	 * 
	 * @param idPokoju identyfikator pokoju, w którym mieszkają sobie studenci
	 * @return lista studentów
	 */
	List<Student> findStudenciWPokoju(long idPokoju);
	
	
	/**
	 * Kasuje wszystkie obiekty Kwaterunek.
	 */
	void deleteAll();
	
	/**
	 * Kasuje wybrany Kwaterunek.
	 * 
	 * @param id identyfikator kasowanego kwaterunku
	 */
	void deleteById(long id);
	
	/**
	 * Zlicza rekordy w tabeli.
	 * 
	 * @return liczba wszystkich rekordów kwaterunków w bazie
	 */
	long count();
}
