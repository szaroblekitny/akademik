package org.wojtekz.akademik.entity;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
		Assert.assertEquals(pokoj.getNumerPokoju(), "11");
		Assert.assertEquals(pokoj.getLiczbaMiejsc(), 4);
	}

}
