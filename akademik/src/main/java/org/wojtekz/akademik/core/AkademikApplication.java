package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Pokoj;

public class AkademikApplication {
	private static Logger logg = Logger.getLogger(AkademikApplication.class.getName());

	public static void main(String[] args) {
		logg.info("----->>> Begin of AkademikApplication");
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AkademikConfiguration.class);
		
		logg.debug("----->>> Mamy kontekst AkademikApplication");
		
		PokojService pokojSrv = (PokojService) applicationContext.getBean("pokojService");
		
		Pokoj pokoj = new Pokoj();
		pokoj.setId(1);
		pokoj.setLiczbaMiejsc(3);
		pokoj.setNumerPokoju("1");
		
		pokojSrv.save(pokoj);
		
		logg.info("----->>> Mamy pokój w akademiku " + pokoj.toString());
		
		
		logg.debug("----->>> Zamykamy kontekst AkademikApplication");
		((AbstractApplicationContext) applicationContext).close();
		logg.info("----->>> End of AkademikApplication");
	}
	
	public static void pobierzPokoje(BufferedReader reader) {
		// TODO wczytywanie listy pokoi (do bazy danych)
	}
	
	public static void pobierzStudentow(BufferedReader reader) {
		// TODO wczytywanie listy studentow
	}
	
	public static void zakwateruj() {
		// TODO dorobiæ kwaterunek
	}
	
	public static void podajStanAkademika(BufferedWriter writer) {
		// TODO wypisanie listy pokoi z zakwaterowanymi studentami
	}
	
	public static void zapiszPokojeDoBufora(BufferedWriter writer) {
		// TODO zrzucenie pokoi z bazy do bufora (plikowego)
	}

	public static void zapiszStudentowDoBufora(BufferedWriter writer) {
		// TODO zrzucenie studentow z bazy do bufora (plikowego)
	}
}
