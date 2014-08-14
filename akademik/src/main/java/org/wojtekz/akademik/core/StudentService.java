package org.wojtekz.akademik.core;

import java.util.List;

import org.wojtekz.akademik.entity.Student;

public interface StudentService {
	public void save(Student student);
	public List<Student> listAll();
	public Student findById(long idStudenta);
	public Student findByName(String name);
	public long iluStudentow();
	public void deleteAll();
	public void deleteById(long idStudenta);

}
