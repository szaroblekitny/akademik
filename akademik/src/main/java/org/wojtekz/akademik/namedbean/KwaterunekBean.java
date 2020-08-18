package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.services.KwaterunekService;

/**
 * Bean JSF do obsługi stronki pokazującej studentów zakwaterowanych
 * w poszczególnych pokojach.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
@Scope("session")
public class KwaterunekBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logg = LogManager.getLogger();
	
	@Autowired
	private transient KwaterunekService kwaterunekService;
	
	/**
	 * Pobiera dane z tabeli kwaterunek i wystawia w formie listy String.
	 * 
	 * @return lista Stringów zwracanych przez Kwaterunek.toString()
	 */
	public List<String> pobierzKwaterunki() {
		logg.debug("-----------> pobierzKwaterunki start");
		List<String> kwaterki = new ArrayList<>();
		List<Kwaterunek> listaKwaterunkow;
		listaKwaterunkow = kwaterunekService.listAll();
		
		for (Kwaterunek ss : listaKwaterunkow) {
			kwaterki.add(ss.toString());
		}
		
		logg.debug("-----------> mamy kwaterunki dla stronki");
		return kwaterki;
	}
	

}
