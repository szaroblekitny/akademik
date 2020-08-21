package org.wojtekz.akademik.services;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wojtekz.akademik.conf.TestConfiguration;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.util.DaneTestowe;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class KwaterunekServiceImplTest {
	private static Logger logg = LogManager.getLogger();
	
	private Kwaterunek kwt1 = new Kwaterunek();
	private Kwaterunek kwt2 = new Kwaterunek();
	
	@Autowired
	private KwaterunekService kwatService;
	
	@Autowired
	private PokojService pokService;
	
	@Autowired
	private StudentService studService;
	
	@Autowired
	private DaneTestowe danne;
	
	@Before
	public void setUp() throws Exception {
		logg.debug("----->>> setupik dla KwaterunekServiceImplTest");
		danne.wrzucTrocheDanychDoBazy();
		logg.trace("----->>> setupik dane wrzucone");
		kwt1.setId(1);
		kwt1.setPokoj(1);
		kwt1.setStudent(2);
		
		kwt2.setId(2);
		kwt2.setPokoj(3);
		kwt2.setStudent(15);
		
		kwatService.save(kwt1);
		kwatService.save(kwt2);
		logg.trace("----->>> setupik koniec setupiku");
	}
	
	@After
	public void setDown() throws Exception {
		kwatService.deleteAll();
		studService.deleteAll();
		pokService.deleteAll();
	}

	@Test
	public void testFindByIdStudenta() {
		logg.debug("----->>> testFindByIdStudenta");
		List<Kwaterunek> kk = kwatService.findByIdStudenta(2);
		Assert.assertEquals(1, kk.get(0).getId());
	}
	
	@Test
	public void testFindById() {
		logg.debug("----->>> testFindByIdStudenta");
		Kwaterunek kk = kwatService.findById(2);
		Assert.assertEquals(3, kk.getPokoj());
	}

	@Test
	public void testFindByIdPokoju() {
		logg.debug("----->>> testFindByIdPokoju");
		// List<Kwaterunek> findByIdPokoju(long idPokoju)
		List<Kwaterunek> lKwa = kwatService.findByIdPokoju(3);
		Assert.assertEquals(15, lKwa.get(0).getStudent());
	}

	@Test
	public void testFindStudenciWPokoju() {
		logg.debug("----->>> testFindStudenciWPokoju");
		
		LoggerContext logCtx = (LoggerContext) LogManager.getContext(false);
		Configuration logConf = logCtx.getConfiguration();
		LoggerConfig loggerConfig = logConf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		Level appLevel = loggerConfig.getLevel();
		loggerConfig.setLevel(Level.TRACE);
		logCtx.updateLoggers();
		
		logg.trace("======> testFindStudenciWPokoju Level.TRACE");
		
		List<Student> stdWPokoju = kwatService.findStudenciWPokoju(1);
		Assert.assertNotNull(stdWPokoju);
		Assert.assertEquals(1, stdWPokoju.size());
		
		logg.trace("----->>> Studenci w pokoju 1:");
		for (Student student: stdWPokoju) {
			logg.trace("----->>> " + student.toString());
		}
		
		Assert.assertEquals("Kowalska", stdWPokoju.get(0).getNazwisko());
		
		loggerConfig.setLevel(appLevel);
		logCtx.updateLoggers();
	}
	
	@Test
	public void testListyWszystkich() {
		logg.debug("----->>> testListyWszystkich");
		List<Kwaterunek> listaKwat = kwatService.listAll();
		logg.debug("----->>> Mamy {} kwaterunków", listaKwat.size());
		Assert.assertEquals(2, listaKwat.size());
	}
	
	@Test
	public void testDeleteById() {
		logg.debug("----->>> testDeleteById");
		kwatService.deleteById(1);
		Assert.assertEquals(1, kwatService.count());
		Assert.assertEquals(1, kwatService.listAll().size());
	}

}
