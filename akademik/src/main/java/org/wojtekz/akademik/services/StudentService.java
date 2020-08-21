package org.wojtekz.akademik.services;

import java.util.List;

import org.wojtekz.akademik.entity.Student;

/**
 * Metody obsługi studenta.
 * 
 * @author Wojtek Zaręba
 *
 */
public interface StudentService {
	
	/**
	 * Zapis studenta do bazy.
	 * 
	 * @param student Obiekt klasy Student
	 */
	void save(Student student);
	
	/**
	 * Zwraca listę wszystkich studentów.
	 * 
	 * @return List&lt;Student&gt;
	 */
	List<Student> listAll();
	
	/**
	 * Zwraca studenta po podaniu identyfikatora ID.
	 * 
	 * @param idStudenta identyfikator w bazie danych
	 * @return znaleziony obiekt studenta
	 */
	Student findById(long idStudenta);
	
	/**
	 * Podaje listę studentów o podanym nazwisku.
	 * 
	 * @param name nazwisko studenta
	 * @return lista studentów o podanym nazwisku
	 */
	List<Student> findByName(String name);
	
	/**
	 * Ilu mamy studentów w bazie.
	 * 
	 * @return liczba rekordów studentów w bazie
	 */
	long iluStudentow();
	
	/**
	 * Czyszczenie do spodu tabeli ze studentami.
	 */
	void deleteAll();
	
	/**
	 * Kasowanie pojedynczego studenta.
	 * 
	 * @param idStudenta identyfikator skazańca
	 */
	void deleteById(long idStudenta);

}
