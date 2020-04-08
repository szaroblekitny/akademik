package org.wojtekz.akademik.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Student;

/**
 * Najprostszy test encji Student.
 *
 * @author Wojciech Zaręba
 */
public class StudentTest {
	private Student student;
	
	@Before
	public void before() {
		student = new Student();
		student.setId(1);
		student.setImie("Wojtek");
		student.setNazwisko("Kowalski");
		student.setPlec(Plec.MEZCZYZNA);
	}

	@Test
	public void test() {
		Assert.assertEquals("Wojtek", student.getImie());
	}

}
