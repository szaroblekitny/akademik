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
	public void save(Student student);
	
	/**
	 * Zwraca listę wszystkich studentów.
	 * 
	 * @return List<Student>
	 */
	public List<Student> listAll();
	
	/**
	 * Zwraca studenta po podaniu identyfikatora ID.
	 * 
	 * @param idStudenta identyfikator w bazie danych
	 * @return znaleziony obiekt studenta
	 */
	public Student findById(long idStudenta);
	
	/**
	 * Podaje listę studentów o podanym nazwisku.
	 * 
	 * @param name nazwisko studenta
	 * @return lista studentów o podanym nazwisku
	 */
	public List<Student> findByName(String name);
	
	/**
	 * Ilu mamy studentów w bazie.
	 * 
	 * @return liczba rekordów studentów w bazie
	 */
	public long iluStudentow();
	
	/**
	 * Czyszczenie do spodu tabeli ze studentami.
	 */
	public void deleteAll();
	
	/**
	 * Kasowanie pojedynczego studenta.
	 * 
	 * @param idStudenta identyfikator skazańca
	 */
	public void deleteById(long idStudenta);

}
