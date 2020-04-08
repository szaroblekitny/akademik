package org.wojtekz.akademik.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Najprostszy test encji Pokoj.
 * 
 * @author wzareba
 *
 */
public class PokojTest {
	Pokoj pokoj;
	
	@Before
	public void setup() {
		pokoj = new Pokoj();
		pokoj.setId(1);
		pokoj.setLiczbaMiejsc(4);
		pokoj.setNumerPokoju("11");
	}

	@Test
	public void test() {
		Assert.assertEquals("11", pokoj.getNumerPokoju());
		Assert.assertEquals(4, pokoj.getLiczbaMiejsc());
	}

}
