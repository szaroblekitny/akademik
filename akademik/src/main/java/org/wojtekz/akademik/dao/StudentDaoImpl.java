package org.wojtekz.akademik.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.wojtekz.akademik.entity.Student;

@Repository("studentDao")
public class StudentDaoImpl implements StudentDao {
	@PersistenceContext
	private EntityManager entityManager;

	public void deleteAllInBatch() {
		deleteAll();		
	}

	public void deleteInBatch(Iterable<Student> entities) {
		// TODO Auto-generated method stub
		
	}

	public List<Student> findAll() {
		List<Student> listaWyn = entityManager.createQuery("from student", Student.class).getResultList();
		return listaWyn;
	}

	public List<Student> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Student> findAll(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	public void flush() {
		entityManager.flush();		
	}

	public Student getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public <S extends Student> List<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	public <S extends Student> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<Student> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(Long id) {
		Student student = findOne(id);
		delete(student);
	}

	public void delete(Student entity) {
		entityManager.remove(entity);
	}

	public void delete(Iterable<? extends Student> entities) {
		for (Student student: entities) {
			delete(student);
		}
		
	}

	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	public boolean exists(Long id) {
		return entityManager.contains(findOne(id));
	}

	public Student findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public <S extends Student> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
