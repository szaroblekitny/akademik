package org.wojtekz.akademik.entity;

import java.util.List;

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
	
	/**
	 * Studentów porządkujemy rosnąco po ID. Liczy się tylko znak wyniku compareTo.
	 */
	@Test
	public void testPorownania() {
		Student student2 = dane.getStudent2();
		Student student5 = dane.getStudent5();
		Assertions.assertTrue(student5.compareTo(student2) > 0, "student o ID 5 powinien być po studencie o ID 2");
		Assertions.assertTrue(student2.compareTo(student5) < 0, "student o ID 2 powinien być przed studentem o ID 5");
		Assertions.assertEquals(0, student2.compareTo(student2), "student porównany sam ze sobą");
	}

	/**
	 * Różnica ID równa 2^32: po rzutowaniu na int zostaje 0.
	 */
	@Test
	public void testPorownaniaDuzychId() {
		Student pierwszy = studentOId(0L);
		Student drugi = studentOId(4_294_967_296L);
		Assertions.assertTrue(pierwszy.compareTo(drugi) < 0, "student o ID 0 powinien być przed studentem o ID 2^32");
	}

	@Test
	public void testSortowania() {
		List<Student> studenci = List.of(studentOId(5L), studentOId(2L), studentOId(4L));
		List<Long> posortowaneId = studenci.stream()
				.sorted()
				.map(Student::getId)
				.toList();
		Assertions.assertEquals(List.of(2L, 4L, 5L), posortowaneId);
	}

	private static Student studentOId(long id) {
		Student student = new Student();
		student.setId(id);
		return student;
	}
	
	@Test
	public void testPorowNull() {
		Student student1 = dane.getStudent1();
		Assertions.assertThrows(NullPointerException.class, () -> student1.compareTo(null));
	}

}
