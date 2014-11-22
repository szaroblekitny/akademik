package org.wojtekz.akademik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Student;

/**
 * Interfejs rozszerzaj¹cy JpaRepository dla klasy Student. Zawiera dodatkowe metody
 * dla obs³ugi Studentów w bazie danych.
 * 
 * @author Wojtek Zarêba
 *
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
	
	/**
	 * Wyszukowanie studentów wg nazwiska (nazwy). Istnieje niejawne za³o¿enie, ¿e istnieje
	 * tylko jeden student o podanej nazwie.
	 * 
	 * @param name nazwisko studenta
	 * @return obiekt Student
	 */
	@Query("select ss from Student ss where ss.nazwisko = ?1")
	public Student findByName(String name);

}
