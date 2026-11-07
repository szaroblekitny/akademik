package org.wojtekz.akademik.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Najprostszy test encji Pokoj.
 * 
 * @author Wojciech Zaręba
 *
 */
public class PokojTest {
	private DaneTestowe dane = new DaneTestowe();
	
	@Test
	public void testOgolny() {
		Assertions.assertEquals("101", dane.getPokoj1().getNumerPokoju());
		Assertions.assertEquals(2, dane.getPokoj1().getLiczbaMiejsc());
	}
	
	@Test
	public void testZakwaterowania() {
		Pokoj pokoik = dane.getPokoj2();
		Assertions.assertNotNull(pokoik);
		Student studenciak = dane.getStudent3();
		Assertions.assertNotNull(studenciak);
		pokoik.zakwateruj(studenciak);
		Assertions.assertEquals(1, pokoik.getZakwaterowani().size());
		Assertions.assertEquals("102", studenciak.getPokoj().getNumerPokoju());
	}
	
	@Test
	public void testWykwaterowania() {
		Pokoj pokoik = dane.getPokoj3();
		Student testowanyStudent = dane.getStudent4();
		Assertions.assertNotNull(pokoik);
		pokoik.zakwateruj(dane.getStudent3());
		pokoik.zakwateruj(testowanyStudent);
		Assertions.assertEquals("103", testowanyStudent.getPokoj().getNumerPokoju());
		Assertions.assertEquals(2, pokoik.getZakwaterowani().size());
		pokoik.wykwateruj(testowanyStudent);
		Assertions.assertEquals(1, pokoik.getZakwaterowani().size());
		Assertions.assertNull(testowanyStudent.getPokoj());
	}
	
	@Test
	public void testPorownania() {
		Pokoj pokoj1 = dane.getPokoj1();
		Pokoj pokoj2 = dane.getPokoj2();
		Assertions.assertEquals(1, pokoj1.compareTo(pokoj2));
	}

}
