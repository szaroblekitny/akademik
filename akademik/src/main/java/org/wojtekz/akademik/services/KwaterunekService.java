package org.wojtekz.akademik.dao;

import java.util.List;

import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Student;


/**
 * Serwis dla klasy (tabeli) Kwaterunek. Zawiera metody obs�ugi rekord�w dla tej tabeli.
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
	 * Lista wszystkich kwaterunk�w w bazie.
	 * @return lista obiekt�w Kwaterunek
	 */
	public List<Kwaterunek> listAll();
	
	/**
	 * Znajduje Kwaterunek po identyfikatorze w�asnym.
	 * 
	 * @param id
	 * @return obiekt Kwaterunek
	 */
	public Kwaterunek findById(long id);
	
	/**
	 * Znajduje Kwaterunek po identyfikatorze studenta. Teoretycznie powinien by� to jeden
	 * rekord (?).
	 * 
	 * @param idStudenta
	 * @return lista obiekt�w Kwaterunek
	 */
	public List<Kwaterunek> findByIdStudenta(long idStudenta);
	
	/**
	 * Lista obiekt�w Kwaterunek o podanym identyfikatorze pokoju.
	 * 
	 * @param idPokoju
	 * @return lista obiekt�w Kwaterunek
	 */
	public List<Kwaterunek> findByIdPokoju(long idPokoju);
	
	/**
	 * Wyszukuje student�w zamieszka�ych w pokoju przez wyszukanie ich kwaterunk�w.
	 * 
	 * @param idPokoju
	 * @return lista student�w
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
