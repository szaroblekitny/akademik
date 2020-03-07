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
	 * @param kwaterunek
	 */
	public void save(Kwaterunek kwaterunek);
	
	/**
	 * Lista wszystkich kwaterunków w bazie.
	 * @return lista obiektów Kwaterunek
	 */
	public List<Kwaterunek> listAll();
	
	/**
	 * Znajduje Kwaterunek po identyfikatorze własnym.
	 * 
	 * @param id
	 * @return obiekt Kwaterunek
	 */
	public Kwaterunek findById(long id);
	
	/**
	 * Znajduje Kwaterunek po identyfikatorze studenta. Teoretycznie powinien być to jeden
	 * rekord (?).
	 * 
	 * @param idStudenta
	 * @return lista obiektów Kwaterunek
	 */
	public List<Kwaterunek> findByIdStudenta(long idStudenta);
	
	/**
	 * Lista obiektów Kwaterunek o podanym identyfikatorze pokoju.
	 * 
	 * @param idPokoju
	 * @return lista obiektów Kwaterunek
	 */
	public List<Kwaterunek> findByIdPokoju(long idPokoju);
	
	/**
	 * Wyszukuje studentów zamieszkałych w pokoju przez wyszukanie ich kwaterunków.
	 * 
	 * @param idPokoju
	 * @return lista studentów
	 */
	public List<Student> findStudenciWPokoju(long idPokoju);
	
	
	/**
	 * Kasuje wszystkie obiekty Kwaterunek.
	 */
	public void deleteAll();
	
	/**
	 * Kasuje wybrany Kwaterunek.
	 * 
	 * @param id
	 */
	public void deleteById(long id);
}
