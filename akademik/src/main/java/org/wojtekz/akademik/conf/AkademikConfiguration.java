package org.wojtekz.akademik.conf;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.wojtekz.akademik.core.AkademikApplication;
import org.wojtekz.akademik.core.Plikowanie;
import org.wojtekz.akademik.core.WlasciwosciPersystencji;
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
public class AkademikConfiguration {
	private static Logger logg = Logger.getLogger(AkademikConfiguration.class.getName());
	
	@Bean(initMethod="init")
	public WlasciwosciPersystencji emfProperties() {
		logg.debug("----->>> WlasciwosciPersystencji bean configuration");
		WlasciwosciPersystencji info = new WlasciwosciPersystencji();
		
		return info;
	}
	
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
	
	
}
