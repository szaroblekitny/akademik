package org.wojtekz.akademik.entity;

import org.junit.Assert;
import org.junit.Test;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Najprostszy test encji Student.
 *
 * @author Wojciech Zaręba
 */
public class StudentTest {
	private DaneTestowe dane = new DaneTestowe();
	
	@Test
	public void testImienia() {
		Assert.assertEquals("Jan", dane.getStudent1().getImie());
	}
	
	@Test
	public void testPorownania() {
		Student student2 = dane.getStudent2();
		Student student5 = dane.getStudent5();
		Assert.assertEquals(-3, student5.compareTo(student2));
	}
	
	@Test(expected = NullPointerException.class)
	public void testPorowNull() {
		Student student1 = dane.getStudent1();
		student1.compareTo(null);
	}

}
