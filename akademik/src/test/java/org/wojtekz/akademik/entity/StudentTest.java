package org.wojtekz.akademik.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
		Assertions.assertEquals("Jan", dane.getStudent1().getImie());
	}
	
	@Test
	public void testPorownania() {
		Student student2 = dane.getStudent2();
		Student student5 = dane.getStudent5();
		Assertions.assertEquals(-3, student5.compareTo(student2));
	}
	
	@Test
	public void testPorowNull() {
		Student student1 = dane.getStudent1();
		Assertions.assertThrows(NullPointerException.class, () -> student1.compareTo(null));
	}

}
