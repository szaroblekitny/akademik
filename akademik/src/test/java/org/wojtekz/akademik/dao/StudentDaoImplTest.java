package org.wojtekz.akademik.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wojtekz.akademik.entity.Student;

public class StudentDaoImplTest {
	private Student student;
	private StudentDao studentDao = new StudentDaoImpl();
	
	@Before
	public void before() {
		student = new Student();
		student.setId(1);
	}

	@Test
	public void findAllTest() {
		Assert.assertNotNull(studentDao.findAll());
	}

}
