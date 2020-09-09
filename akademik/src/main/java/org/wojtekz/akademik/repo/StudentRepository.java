package org.wojtekz.akademik.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Pokoj;
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
	 * Wyszukowanie studentów wg nazwiska (nazwy).
	 * 
	 * @param name nazwisko studenta
	 * @return obiekt Student
	 */
	@Query("select ss from Student ss where ss.nazwisko = ?1")
	List<Student> findByName(String name);

	/**
	 * Wyszukiwanie studentów z tego samego pokoju.
	 * 
	 * @param pokoj rzeczony pokój
	 * @return lista mieszkańców pokoju
	 */
	List<Student> findByPokoj(Pokoj pokoj);
}
