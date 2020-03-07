package org.wojtekz.akademik.conf;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Ta klasa konfiguruje "produkcyjne" (nietestowe) połączenie z bazą danych.
 * Do testów konfiguracja będzie brana z TestConfiguration.
 * 
 * @author wojtek
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("org.wojtekz.akademik.repos")
@Import(AkademikConfiguration.class)
public class MainConfiguration {
	private static Logger logg = LogManager.getLogger();
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		logg.debug("----->>> LocalContainerEntityManagerFactoryBean bean configuration");
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setPackagesToScan("org.wojtekz.akademik.entity");
		em.setPersistenceUnitName("unitPU");

		return em;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		logg.debug("----->>> PlatformTransactionManager bean configuration");
		JtaTransactionManager transactionManager = new JtaTransactionManager();

		return transactionManager;
	}

}
