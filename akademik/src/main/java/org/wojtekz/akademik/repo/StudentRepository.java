package org.wojtekz.akademik.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Plec;
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
	 * Wyszukiwanie studentów z tego samego pokoju. Dla standardowych
	 * wyszukiwań po pojedynczych polach nie trzeba podawać query - biblioteka
	 * sama sobie je wymyśla (powiedzmy sobie, że nie jest to wielka sztuka).
	 * 
	 * @param pokoj rzeczony pokój
	 * @return lista mieszkańców pokoju
	 */
	List<Student> findByPokoj(Pokoj pokoj);
	
	/**
	 * Wyszukiwanie studentów wg płci. Najpierw kwaterujemy kobiety,
	 * potem mężczyzn, więc musimy ich sobie wyłapać.
	 * 
	 * @param plec płeć szukanych studentów
	 * @return lista studentek lub studentów
	 */
	List<Student> findByPlec(Plec plec);
	
	/**
	 * Szuka rekordu z największym ID.
	 * 
	 * @return wartość największego ID rekordu istniejącego w bazie
	 */
	@Query("SELECT MAX(st.id) FROM Student st")
	Long findLastId();

}
