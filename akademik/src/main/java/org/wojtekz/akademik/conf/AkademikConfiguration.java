package org.wojtekz.akademik.conf;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.wojtekz.akademik.core.AkademikApplication;
import org.wojtekz.akademik.core.KwaterunekService;
import org.wojtekz.akademik.core.KwaterunekServiceImpl;
import org.wojtekz.akademik.core.Plikowanie;
import org.wojtekz.akademik.core.PokojService;
import org.wojtekz.akademik.core.PokojServiceImpl;
import org.wojtekz.akademik.core.StudentService;
import org.wojtekz.akademik.core.StudentServiceImpl;
import org.wojtekz.akademik.util.DaneTestowe;

/**
 * Klasa konfiguracyjna aplikacji. Zawiera definicje bean�w springa w formie klasy,
 * co oznacza, �e nie jest konieczny plik konfiguracyjny xml. W plikach s� konfiguracje
 * persystencji, dost�pu do danych i logowania log4j.
 * 
 * @author Wojtek Zar�ba
 *
 */
@Configuration
@ComponentScan(basePackages={"org.wojtekz.akademik.core"})
@ImportResource("classpath:config_dao.xml")
public class AkademikConfiguration {
	private static Logger logg = Logger.getLogger(AkademikConfiguration.class.getName());
	
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
	Plikowanie plikowanie() {
		logg.debug("----->>> plikowanie bean configuration");
		Plikowanie plikowanie = new Plikowanie();
		plikowanie.setMarshaller(xStreamMarshaller());
		plikowanie.setUnmarshaller(xStreamMarshaller());
		return plikowanie;
	}
	
	@Bean
	XStreamMarshaller xStreamMarshaller() {
		logg.debug("----->>> xStreamMarshaller bean configuration");
		// XStreamMarshaller xsm = new XStreamMarshaller();
		// xsm.
		return new XStreamMarshaller();
	}
	
	@Bean
	AkademikApplication akademikApplication() {
		logg.debug("----->>> akademikApplication bean configuration");
		return new AkademikApplication();
	}
	
	@Bean
	DaneTestowe daneTestowe() {
		logg.debug("----->>> daneTestowe bean configuration");
		return new DaneTestowe();
	}
}
