package org.wojtekz.akademik.entity;

import org.junit.Assert;
import org.junit.Test;
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
		Assert.assertEquals("101", dane.getPokoj1().getNumerPokoju());
		Assert.assertEquals(2, dane.getPokoj1().getLiczbaMiejsc());
	}
	
	@Test
	public void testZakwaterowania() {
		Pokoj pokoik = dane.getPokoj2();
		Assert.assertNotNull(pokoik);
		Student studenciak = dane.getStudent3();
		Assert.assertNotNull(studenciak);
		pokoik.zakwateruj(studenciak);
		Assert.assertEquals(1, pokoik.getZakwaterowani().size());
		Assert.assertEquals("102", studenciak.getPokoj().getNumerPokoju());
	}
	
	@Test
	public void testWykwaterowania() {
		Pokoj pokoik = dane.getPokoj3();
		Student testowanyStudent = dane.getStudent4();
		Assert.assertNotNull(pokoik);
		pokoik.zakwateruj(dane.getStudent3());
		pokoik.zakwateruj(testowanyStudent);
		Assert.assertEquals("103", testowanyStudent.getPokoj().getNumerPokoju());
		Assert.assertEquals(2, pokoik.getZakwaterowani().size());
		pokoik.wykwateruj(testowanyStudent);
		Assert.assertEquals(1, pokoik.getZakwaterowani().size());
		Assert.assertNull(testowanyStudent.getPokoj());
	}
	
	@Test
	public void testPorownania() {
		Pokoj pokoj1 = dane.getPokoj1();
		Pokoj pokoj2 = dane.getPokoj2();
		Assert.assertEquals(1, pokoj1.compareTo(pokoj2));
	}

}
