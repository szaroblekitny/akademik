package org.wojtekz.akademik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wojtekz.akademik.entity.Student;

public interface StudentDao extends JpaRepository<Student, Long> {

}
