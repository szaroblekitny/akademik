package org.wojtekz.akademik.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.dao.PokojService;
import org.wojtekz.akademik.entity.Pokoj;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
public class PokojServiceImplTest {
	private static Logger logg = Logger.getLogger(PokojServiceImplTest.class.getName());
	
	Pokoj pokoj;
	
	@Autowired
	PokojService pokService;
	
	@Before
	public void before() {
		logg.debug("----->>> Pokoj before method fired");
		pokoj = new Pokoj();
		pokoj.setId(1);
	}
	
	@After
	public void after() {
		logg.debug("----->>> Pokoj after method fired");
		pokService.deleteAll();
	}

	@Test
	public void testSave() {
		logg.debug("----->>> testSave");
		pokService.save(pokoj);
		Assert.assertEquals(1, pokService.ilePokoi());
	}

	@Test
	public void testListAll() {
		logg.debug("----->>> testListAll");
		List<Pokoj> pokoje = pokService.listAll();
		Assert.assertNotNull(pokoje);
	}

	@Test
	public void testFindByNumber() {
		logg.debug("----->>> testFindByNumber");
		
		pokoj.setLiczbaMiejsc(3);
		pokoj.setNumerPokoju("1");
		pokService.save(pokoj);
		
		Pokoj pokoik;
		pokoik = pokService.findByNumber("1");
		
		Assert.assertEquals(3, pokoik.getLiczbaMiejsc());
	}

	@Test
	public void testIlePokoi() {
		logg.debug("----->>> testIlePokoi");
		Assert.assertEquals(0, pokService.ilePokoi());
	}

}
