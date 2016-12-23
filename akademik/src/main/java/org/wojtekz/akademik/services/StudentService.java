package org.wojtekz.akademik.services;

import java.util.List;

import org.wojtekz.akademik.entity.Student;

/**
 * Metody obs³ugi studenta.
 * 
 * @author Wojtek
 *
 */
public interface StudentService {
	
	/**
	 * Zapis studenta do bazy.
	 * 
	 * @param student Obiekt klasy Student
	 */
	public void save(Student student);
	public List<Student> listAll();
	public Student findById(long idStudenta);
	public Student findByName(String name);
	public long iluStudentow();
	public void deleteAll();
	public void deleteById(long idStudenta);

}
