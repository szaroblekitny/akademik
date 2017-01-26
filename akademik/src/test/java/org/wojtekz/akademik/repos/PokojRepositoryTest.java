package org.wojtekz.akademik.repos;

import java.util.List;

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
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Pokoj;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
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
	
	@Test
	public void zapiszIOdczyt() {
		List<Pokoj> listaPokoi;
		logg.debug("----->>> zapiszIOdczyt pokoj method fired");
		pokojRep.deleteAll();
		logg.debug("----->>> zapiszIOdczyt po deleteAll");
		pokojRep.save(pokoj);
		logg.debug("----->>> zapiszIOdczyt po save pokoj");
		listaPokoi = pokojRep.findAll();
		logg.debug("----->>> zapiszIOdczyt po findAll");
		Assert.assertEquals(1, listaPokoi.size());
		
	}

}
