package org.wojtekz.akademik.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repos.StudentRepository;

/**
 * Implementacja interfejsu StudentService.
 * 
 * @author Wojtek Zaręba
 *
 */
@Repository
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {
	private static Logger logg = LogManager.getLogger();
	
	@Autowired
	StudentRepository studentRep;

	@Override
	@Transactional
	public void save(Student student) {
		logg.trace("----->>> save Student {}", student);
		studentRep.save(student);

	}

	@Override
	public List<Student> listAll() {
		logg.trace("----->>> listAll Student");
		
		return studentRep.findAll();
	}

	@Override
	public Student findById(long idStudenta) {
		logg.trace("----->>> findById dla studenta {}", idStudenta);
		return studentRep.findOne(idStudenta);
	}

	@Override
	public List<Student> findByName(String name) {
		logg.trace("----->>> findByName Student {}", name);
		
		return studentRep.findByName(name);
	}

	@Override
	public long iluStudentow() {
		logg.trace("----->>> iluStudentow");
		
		return studentRep.count();
	}

	@Override
	@Transactional
	public void deleteAll() {
		logg.trace("----->>> deleteAll Student");
		
		studentRep.deleteAll();
	}

	@Override
	@Transactional
	public void deleteById(long idStudenta) {
		logg.trace("----->>> deleteById Student {}", idStudenta);
		
		studentRep.delete(idStudenta);
	}

}
