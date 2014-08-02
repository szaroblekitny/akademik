package org.wojtekz.akademik.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.entity.Student;

@Repository("studentDao")
@Transactional(readOnly = true)
public class StudentDaoImpl implements StudentDao {
	@PersistenceContext
	private EntityManager entityManager;
	
	private static Logger logg = Logger.getLogger(StudentDaoImpl.class.getName());

	@Transactional
	public void deleteAllInBatch() {
		deleteAll();		
	}

	@Transactional
	public void deleteInBatch(Iterable<Student> entities) {
		// TODO Auto-generated method stub
		
	}

	public List<Student> findAll() {
		logg.debug("----->>> findAll method fired");
		// List<Student> listaWyn = entityManager.createQuery(Student.class).getResultList();
		List<Student> listaWyn = entityManager.createQuery("from Student", Student.class).getResultList();
		if (logg.isDebugEnabled()) {
			for (Student st : listaWyn) {
				logg.debug("----->>> student: " + st.toString());
			}
		}
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

	@Transactional
	public void flush() {
		entityManager.flush();		
	}

	public Student getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	public <S extends Student> List<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
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

	@Transactional
	public void delete(Long id) {
		Student student = findOne(id);
		delete(student);
	}

	@Transactional
	public void delete(Student entity) {
		entityManager.remove(entity);
	}

	@Transactional
	public void delete(Iterable<? extends Student> entities) {
		for (Student student: entities) {
			delete(student);
		}
		
	}

	@Transactional
	public void deleteAll() {
		logg.debug("----->>> deleteAll students");
		
		CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete<Student> deleteQuery = critBuilder.createCriteriaDelete(Student.class);
		
		Root<Student> root = deleteQuery.from(Student.class);
		
		deleteQuery.where(critBuilder.isTrue(root.isNotNull()));
		
		logg.debug("----->>> deleteAll students after where condition");
		
		entityManager.createQuery(deleteQuery).executeUpdate();
		
		logg.debug("----->>> deleteAll students after execute query");
	}

	public boolean exists(Long id) {
		logg.debug("----->>> exists(Long id) student");
		return entityManager.contains(findOne(id));
	}

	public Student findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Zapisuje studenta do bazy. Zwaraca tego samego studenta.
	 * 
	 * @return student wziêty z parametru wejœciowego
	 */
	@Transactional
	public <S extends Student> S save(S entity) {
		entityManager.persist(entity);
		return entity;
	}

}
