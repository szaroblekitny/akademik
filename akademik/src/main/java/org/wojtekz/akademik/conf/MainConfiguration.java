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
import org.springframework.context.annotation.DependsOn;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * Ta klasa konfiguruje "produkcyjne" (nietestowe) połączenie z bazą danych.
 * Do testów konfiguracja jest brana z TestConfiguration.
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
     * DataSource. Pobiera JNDI dla naszej aplikacji.
     * 
     * @return DataSource brane z zasobu określonego przez JNDI
     * @throws NamingException
     */
    @Bean
    public DataSource dataSource() throws NamingException {
    	logg.debug("----->>> DataSource bean configuration");
        return (DataSource) new JndiTemplate().lookup("java:comp/env/jdbc/akademik");
    }
	
	/**
	 * Właściwości dla Hibernejta. W zasadzie powinny być w pliku, 
	 * ale tu jest na razie łatwiej.
	 * 
	 * @return właściwosci sterujące Hibernejtem
	 */
	private Properties hibernateProperties() {
	    Properties properties = new Properties();
	    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
	    properties.setProperty("hibernate.transaction.coordinator_class", "jta");
	    properties.setProperty("hibernate.transaction.jta.platform", "Atomikos");
	    properties.setProperty("hibernate.jta.allowTransactionAccess", "true");
	    properties.setProperty("hibernate.generate_statistics", "false");
	    properties.setProperty("hibernate.default_batch_fetch_size", "2");
	    properties.setProperty("hibernate.show_sql", "true");
	    properties.setProperty("hibernate.format_sql", "true");
	    properties.setProperty("hibernate.use_sql_comments", "true");
	    
	    return properties;
	}
	
	/**
	 * Właściwości dla Atomikosa. Muszą być przekazane do konstruktora
	 * UserTransactionServiceImp.
	 * 
	 * @return właściwości wymagane przez Atomikosa
	 */
	private Properties atomikosProperties() {
		Properties atoProps = new Properties();
		atoProps.setProperty("com.atomikos.icatch.service", "com.atomikos.icatch.standalone.UserTransactionServiceFactory");

		return atoProps;
	}


	/**
	 * Obstługa bazy danych przez uniwersalny kontener. Być może trzeba będzie zmienić,
	 * ale na razie to działa.
	 * 
	 * @return LocalContainerEntityManagerFactoryBean z włączonym Hibernejtem
	 *         i szukaniem źródła danych przez JNDI
	 */
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
    	em.setJpaProperties(hibernateProperties());
	  
    	return em;
	}
    
    /**
     * Serwis Atomikosa do obsługi transakcji.
     * 
     * @return UserTransactionService
     */
    @Bean(initMethod = "init", destroyMethod = "shutdownForce")
    public UserTransactionService userTransactionService() {
    	UserTransactionService uts = new UserTransactionServiceImp(atomikosProperties());
    	
    	return uts;
    }
	
    /**
     * UserTransactionManager Atomikosa dla JTA.
     * 
     * @return UserTransactionManager
     */
	@Bean(initMethod = "init", destroyMethod = "close")
	@DependsOn("userTransactionService")
	public UserTransactionManager atomikosTransactionManager() {
		logg.debug("----->>> UserTransactionManager bean configuration");
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		
		// disable startup because the userTransactionService above does this
		userTransactionManager.setStartupTransactionService(false);
		// when close is called, should we force transactions to terminate, or not?
		userTransactionManager.setForceShutdown(true);
		
		return userTransactionManager;
	}
	
	/**
	 * JTA UserTransaction Atomikosa. Błąd jest przechwytywany, ale może to błąd?
	 * 
	 * @return UserTransaction
	 */
	@Bean
	@DependsOn("userTransactionService")
	UserTransaction userTransaction() {
		logg.debug("----->>> UserTransaction bean configuration");
		UserTransaction userTransaction = new UserTransactionImp();
		try {
			userTransaction.setTransactionTimeout(300);
		} catch (SystemException se) {
			logg.error("----->>> STRASZNY BŁĄD TRANSAKCJI", se);
		}
		return userTransaction;
	}
	
	/**
	 * PlatformTransactionManager dla aplikacji skonfigurowany jako JTA
	 * i obsługiwany przez Atomikosa.
	 * 
	 * @return PlatformTransactionManager
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		logg.debug("----->>> PlatformTransactionManager bean configuration");
		
		JtaTransactionManager transactionManager = new JtaTransactionManager();
		transactionManager.setUserTransaction(userTransaction());
		transactionManager.setTransactionManager(atomikosTransactionManager());
		
		return transactionManager;
	}

}
