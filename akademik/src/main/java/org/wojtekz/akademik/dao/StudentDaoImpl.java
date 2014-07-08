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

	@Override
	public void deleteAllInBatch() {
		deleteAll();
	}

	@Override
	public void deleteInBatch(Iterable<Student> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Student> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Student> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Student> findAll(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public Student getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Student> List<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Student> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Student> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Long id) {
		Student student = findOne(id);
		delete(student);
	}

	@Override
	public void delete(Student entity) {
		entityManager.remove(entity);
	}

	@Override
	public void delete(Iterable<? extends Student> entities) {
		for (Student student: entities) {
			delete(student);
		}

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean exists(Long id) {
		return entityManager.contains(findOne(id));
	}

	@Override
	public Student findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Student> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
