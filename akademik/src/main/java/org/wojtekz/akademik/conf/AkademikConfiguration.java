package org.wojtekz.akademik.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.wojtekz.akademik.services.KwaterunekService;
import org.wojtekz.akademik.services.KwaterunekServiceImpl;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.PokojServiceImpl;
import org.wojtekz.akademik.services.StudentService;
import org.wojtekz.akademik.services.StudentServiceImpl;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Klasa konfiguracyjna aplikacji. Zawiera definicje beanów springa w formie klasy,
 * co oznacza, że nie jest konieczny plik konfiguracyjny xml. W plikach są konfiguracje
 * persystencji, dostępu do danych i logowania log4j.
 * 
 * @author Wojtek Zaręba
 *
 */
@Configuration
@ComponentScan(basePackages={"org.wojtekz.akademik.core", "org.wojtekz.akademik.namedbean"})
@EnableJpaRepositories("org.wojtekz.akademik.repos")
public class AkademikConfiguration {
	private static Logger logg = LogManager.getLogger();
	
	@Bean
	MutablePersistenceUnitInfo mutablePersistenceUnitInfo() {
		logg.debug("----->>> MutablePersistenceUnitInfo bean configuration");
		return new MutablePersistenceUnitInfo();
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
	
	@Bean
	DaneTestowe daneTestowe() {
		logg.debug("----->>> daneTestowe bean configuration");
		return new DaneTestowe();
	}
	
	@Bean
	Marshaller marshaller() {
		logg.debug("----->>> xStreamMarshaller bean configuration");
		return new XStreamMarshaller();
	}
	
	@Bean
	Unmarshaller unmarshaller() {
		logg.debug("----->>> xStreamMarshaller bean configuration");
		return new XStreamMarshaller();
	}
	
}
