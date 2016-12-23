package org.wojtekz.akademik.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repos.StudentRepository;

/**
 * Implementacja interfejsu StudentService.
 * 
 * @author Wojtek Zarêba
 *
 */
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {
	private static Logger logg = Logger.getLogger(StudentServiceImpl.class.getName());
	
	@Autowired
	StudentRepository studentRep;

	@Override
	@Transactional
	public void save(Student student) {
		logg.debug("----->>> save Student");
		studentRep.save(student);

	}

	@Override
	public List<Student> listAll() {
		logg.debug("----->>> listAll Student");
		
		return studentRep.findAll();
	}

	@Override
	public Student findById(long idStudenta) {
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> findById " + idStudenta);
		}
		return studentRep.findOne(idStudenta);
	}

	@Override
	public Student findByName(String name) {
		logg.debug("----->>> findByName Student");
		
		return studentRep.findByName(name);
	}

	@Override
	public long iluStudentow() {
		logg.debug("----->>> iluStudentow");
		
		return studentRep.count();
	}

	@Override
	@Transactional
	public void deleteAll() {
		logg.debug("----->>> deleteAll Student");
		
		studentRep.deleteAll();
	}

	@Override
	@Transactional
	public void deleteById(long idStudenta) {
		logg.debug("----->>> deleteById Student");
		
		studentRep.delete(idStudenta);
	}

}
