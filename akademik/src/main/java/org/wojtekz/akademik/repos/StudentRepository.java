package org.wojtekz.akademik.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Student;

/**
 * Interfejs rozszerzający JpaRepository dla klasy Student. Zawiera dodatkowe metody
 * dla obsługi Studentów w bazie danych.
 * 
 * @author Wojtek Zaręba
 *
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
	
	/**
	 * Wyszukowanie studentów wg nazwiska (nazwy). Istnieje niejawne założenie, że istnieje
	 * tylko jeden student o podanej nazwie.
	 * 
	 * @param name nazwisko studenta
	 * @return obiekt Student
	 */
	@Query("select ss from Student ss where ss.nazwisko = ?1")
	public Student findByName(String name);

}
