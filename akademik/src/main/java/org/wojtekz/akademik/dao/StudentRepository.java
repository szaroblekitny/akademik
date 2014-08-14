package org.wojtekz.akademik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
	
	@Query("select ss from Student ss where ss.nazwisko = ?1")
	public Student findByName(String name);

}
