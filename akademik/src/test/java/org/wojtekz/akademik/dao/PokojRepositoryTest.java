package org.wojtekz.akademik.dao;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Pokoj;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AkademikConfiguration.class})
@EnableTransactionManagement
@Transactional
public class PokojRepositoryTest {
	private static Logger logg = Logger.getLogger(PokojRepositoryTest.class.getName());
	
	Pokoj pokoj;
	
	@Autowired
	PokojRepository pokojRep;
	
	@Before
	public void before() {
		logg.debug("----->>> Pokoj before method fired");
		pokoj = new Pokoj();
		pokoj.setId(1);
	}

	@Test
	public void findAllTest() {
		logg.debug("----->>> Pokoj findAllTest method fired");
		Assert.assertNotNull(pokojRep.findAll());
	}

}
