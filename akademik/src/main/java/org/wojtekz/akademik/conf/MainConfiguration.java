package org.wojtekz.akademik.conf;

import org.apache.logging.log4j.Logger;

import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.logging.log4j.LogManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
	
	/**
	 * Właściwości dla Hibernejta. W zasadzie powinny być w pliku, 
	 * ale tu jest na razie łatwiej.
	 * 
	 * @return właściwosci sterujące Hibernejtem
	 */
	Properties hibernatelProperties() {
	    Properties properties = new Properties();
	    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
	    properties.setProperty("hibernate.generate_statistics", "false");
	    properties.setProperty("hibernate.default_batch_fetch_size", "2");
	    properties.setProperty("hibernate.show_sql", "true");
	    properties.setProperty("hibernate.format_sql", "true");
	    properties.setProperty("hibernate.use_sql_comments", "true");
	    
	    return properties;
	}
	
    @Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		logg.debug("----->>> LocalContainerEntityManagerFactoryBean bean configuration");
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setPackagesToScan("org.wojtekz.akademik.entity");
		try {
			em.setJtaDataSource(dataSource());
		} catch (NamingException ne) {
			logg.error("=====>> Błąd szukania bazy danych!");
		}
		
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(hibernatelProperties());

		return em;
	}
    
    @Bean
    public DataSource dataSource() throws NamingException {
    	logg.debug("----->>> DataSource bean configuration");
        return (DataSource) new JndiTemplate().lookup("java:comp/env/jdbc/akademik");
    }
	
	@Bean
	public UserTransactionManager atomikosTransactionManager() {
		logg.debug("----->>> UserTransactionManager bean configuration");
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		try {
			userTransactionManager.init();
		} catch (SystemException se) {
			logg.error("----->>> STRASZNY BŁĄD inita UserTransactionManagera", se);
		}
		
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
