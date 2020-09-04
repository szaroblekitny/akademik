package org.wojtekz.akademik.repo;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Pokoj;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class PokojRepositoryTest {
	private static Logger logg = LogManager.getLogger();
	
	private PokojRepository pokojRep;
	private StudentRepository studenciaki;
	private Pokoj pokoj;
	
	@Autowired
	public void setPokojRep(PokojRepository pokojRep) {
		this.pokojRep = pokojRep;
	}

	@Autowired
	public void setStudenciaki(StudentRepository studenciaki) {
		this.studenciaki = studenciaki;
	}


	/**
	 * Wykonuje czynności przygotowawcze przed każdym testem.
	 * Haczyk jest w tym, że nie można skasować pokoi zanim nie skasuje się
	 * studentów, czyli najpierw liczy pokoje w bazie i jeśli są, kasuje
	 * studentów i pokoje - w tej kolejności.
	 * Potem tworzy jeden pokój testowy wypełniając tylko pole ID
	 * i zapisuje go w bazie.
	 * 
	 */
	@Before
	public void before() {
		logg.debug("-----+> Pokoj before początek");
		List<Pokoj> pokoje;
		
		long ile = pokojRep.count();
		if (ile > 0L) {
			pokoje = pokojRep.findAll();
			logg.trace("Mamy pokoje =====>>> {}", Arrays.toString(pokoje.toArray()));
			studenciaki.deleteAll();
			pokojRep.deleteAll();
		}
		
		pokoj = new Pokoj();
		pokoj.setId(1);
		pokojRep.save(pokoj);
		logg.debug("-----+> Pokoj before koniec");
	}


	@Test
	public void findAllTest() {
		logg.debug("--------> Pokoj findAllTest method fired");
		
		// test początkowy, czy mamy połączenie z bazą - to nie jest oczywiste :-)
		Assert.assertNotNull(pokojRep.findAll());
		
		Assert.assertEquals(1L, pokojRep.findAll().get(0).getId());
		// czyszczenie przed kolejnym testem
		pokojRep.deleteAll();
	}
	
	@Test
	public void zapiszIOdczyt() {
		List<Pokoj> listaPokoi;
		logg.debug("----->>> zapiszIOdczyt pokoj method fired");
		pokojRep.save(pokoj);
		logg.trace("----->>> zapiszIOdczyt po save pokoj");
		listaPokoi = pokojRep.findAll();
		Assert.assertEquals(1, listaPokoi.size());
		pokojRep.deleteAll();
	}

}
