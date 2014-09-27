package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.oxm.XmlMappingException;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Pokoj;

public class AkademikApplication {
	private static Logger logg = Logger.getLogger(AkademikApplication.class.getName());
	
	@Autowired
	static PokojService pokojService;

	public static void main(String[] args) {
		logg.info("----->>> Begin of AkademikApplication");
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AkademikConfiguration.class);
		
		logg.debug("----->>> Mamy kontekst AkademikApplication");
		
		// PokojService pokojSrv = (PokojService) applicationContext.getBean("pokojService");
		
		Pokoj pokoj = new Pokoj();
		pokoj.setId(1);
		pokoj.setLiczbaMiejsc(3);
		pokoj.setNumerPokoju("1");
		
		pokojService.save(pokoj);
		
		logg.info("----->>> Mamy pokój w akademiku " + pokoj.toString());
		
		
		logg.debug("----->>> Zamykamy kontekst AkademikApplication");
		((AbstractApplicationContext) applicationContext).close();
		logg.info("----->>> End of AkademikApplication");
	}
	
	@SuppressWarnings("unchecked")
	public static void pobierzPokoje(BufferedReader reader) throws XmlMappingException, IOException {
		logg.info("----->>> pobierzPokoje begins");
		List<Pokoj> pokoje = new ArrayList<>();
		pokoje = (List<Pokoj>) Plikowanie.loadObjectList(reader);
		for (Pokoj pok : pokoje) {
			pokojService.save(pok);
		}
		logg.info("----->>> pokoje zapisane");
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
