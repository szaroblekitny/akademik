package org.wojtekz.akademik.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.junit.Assert;
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
//ApplicationContext will be loaded from AppConfig and TestConfig
//@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@ContextConfiguration(classes = {AkademikConfiguration.class})
@EnableTransactionManagement
@Transactional
public class PokojDaoImplTest {
	private static Logger logg = Logger.getLogger(PokojDaoImplTest.class.getName());
	
	Pokoj pokoj;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	PokojDao pokojDao = new PokojDaoImpl(Pokoj.class, em);

	@Test
	public void findAllTest() {
		logg.debug("----->>> findAllTest method fired");
		Assert.assertNotNull(pokojDao.findAll());
	}

}
