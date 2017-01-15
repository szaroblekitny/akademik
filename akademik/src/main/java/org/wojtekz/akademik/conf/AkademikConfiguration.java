package org.wojtekz.akademik.conf;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.postgresql.jdbc3.Jdbc3SimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.wojtekz.akademik.core.AkademikApplication;
import org.wojtekz.akademik.core.Plikowanie;
import org.wojtekz.akademik.services.KwaterunekService;
import org.wojtekz.akademik.services.KwaterunekServiceImpl;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.PokojServiceImpl;
import org.wojtekz.akademik.services.StudentService;
import org.wojtekz.akademik.services.StudentServiceImpl;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Klasa konfiguracyjna aplikacji. Zawiera definicje beanów springa w formie klasy,
 * co oznacza, ¿e nie jest konieczny plik konfiguracyjny xml. W plikach s¹ konfiguracje
 * persystencji, dostêpu do danych i logowania log4j.
 * 
 * @author Wojtek Zarêba
 *
 */
@Configuration
@ComponentScan(basePackages={"org.wojtekz.akademik.core", "org.wojtekz.akademik.namedbean"})
@EnableTransactionManagement
@EnableJpaRepositories("org.wojtekz.akademik.repos")
public class AkademikConfiguration {
	private static Logger logg = Logger.getLogger(AkademikConfiguration.class.getName());
	
	@Bean
	Jdbc3SimpleDataSource postgresDataSource() {
		logg.debug("----->>> Jdbc3SimpleDataSource bean configuration");
		Jdbc3SimpleDataSource source = new Jdbc3SimpleDataSource();
		
		return source;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		logg.debug("----->>> LocalContainerEntityManagerFactoryBean bean configuration");
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(postgresDataSource());
		em.setPackagesToScan("org.wojtekz.akademik.entity");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaDialect(new HibernateJpaDialect());
		em.setPersistenceUnitName("postgresPU");

		return em;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		logg.debug("----->>> PlatformTransactionManager bean configuration");
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		logg.debug("----->>> PersistenceExceptionTranslationPostProcessor bean configuration");
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	@Bean
	PokojService pokojService() {
		logg.debug("----->>> pokojService bean configuration");
		return new PokojServiceImpl();
	}
	
	@Bean
	StudentService studentService() {
		logg.debug("----->>> studentService bean configuration");
		return new StudentServiceImpl();
	}
	
	@Bean
	KwaterunekService kwaterunekService() {
		logg.debug("----->>> kwaterunekService bean configuration");
		return new KwaterunekServiceImpl();
	}
	
	/*@Bean
	Plikowanie plikowanie() {
		logg.debug("----->>> plikowanie bean configuration");
		Plikowanie plikowanie = new Plikowanie();
		plikowanie.setMarshaller(xStreamMarshaller());
		plikowanie.setUnmarshaller(xStreamMarshaller());
		return plikowanie;
	}*/
	
	/*@Bean
	XStreamMarshaller xStreamMarshaller() {
		logg.debug("----->>> xStreamMarshaller bean configuration");
		// XStreamMarshaller xsm = new XStreamMarshaller();
		// xsm.
		return new XStreamMarshaller();
	}*/
	
	/*@Bean
	AkademikApplication akademikApplication() {
		logg.debug("----->>> akademikApplication bean configuration");
		return new AkademikApplication();
	}*/
	
	/*@Bean
	DaneTestowe daneTestowe() {
		logg.debug("----->>> daneTestowe bean configuration");
		return new DaneTestowe();
	}*/
}
