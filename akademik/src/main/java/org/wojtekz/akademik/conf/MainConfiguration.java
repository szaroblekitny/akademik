package org.wojtekz.akademik.conf;

import org.apache.logging.log4j.Logger;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.logging.log4j.LogManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * Ta klasa konfiguruje "produkcyjne" (nietestowe) połączenie z bazą danych.
 * Do testów konfiguracja będzie brana z TestConfiguration.
 * 
 * @author Wojciech Zaręba
 *
 */
@Configuration
@EnableTransactionManagement
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
	public UserTransactionManager atomikosTransactionManager() {
		logg.debug("----->>> UserTransactionManager bean configuration");
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		
		// when close is called, should we force transactions to terminate, or not?
		userTransactionManager.setForceShutdown(false);
		
		return userTransactionManager;
	}
	
	@Bean
	UserTransaction userTransaction() {
		logg.debug("----->>> UserTransaction bean configuration");
		UserTransaction userTransaction = new UserTransactionImp();
		try {
			userTransaction.setTransactionTimeout(300);
		} catch (SystemException se) {
			logg.error("----->>> STRASZNY BŁĄD SERWISU TRANSAKCJI", se);
		}
		return userTransaction;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		logg.debug("----->>> PlatformTransactionManager bean configuration");
		JtaTransactionManager transactionManager = new JtaTransactionManager();
		transactionManager.setUserTransaction(userTransaction());
		transactionManager.setTransactionManager(atomikosTransactionManager());
		
		return transactionManager;
	}

}
