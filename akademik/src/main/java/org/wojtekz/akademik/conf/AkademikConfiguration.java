package org.wojtekz.akademik.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Klasa konfiguracyjna aplikacji. Zawiera definicje beanów springa w formie klasy,
 * co oznacza, że nie jest konieczny plik konfiguracyjny xml do definiowania beanów Springa.
 * <p>W plikach są konfiguracje persystencji, dostępu do danych i logowania log4j.
 * 
 * @author Wojtek Zaręba
 *
 */
@Configuration
@ComponentScan(basePackages={"org.wojtekz.akademik.core", "org.wojtekz.akademik.namedbean"})
@EnableJpaRepositories("org.wojtekz.akademik.repo")
public class AkademikConfiguration {
	private static Logger logg = LogManager.getLogger();
	
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		logg.debug("----->>> PersistenceExceptionTranslationPostProcessor bean configuration");
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	@Bean
	public DaneTestowe daneTestowe() {
		logg.debug("----->>> daneTestowe bean configuration");
		return new DaneTestowe();
	}
	
	@Bean
	public Marshaller marshaller() {
		logg.debug("----->>> marshaller bean configuration");
		
		AkademikXStream xsmarsh = new AkademikXStream();
		
		return xsmarsh;
	}
	
	@Bean
	public Unmarshaller unmarshaller() {
		logg.debug("----->>> unmarshaller bean configuration");
		
		return (Unmarshaller) marshaller();
	}
	
}
