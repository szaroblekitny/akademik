package org.wojtekz.akademik.namedbean;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.core.Akademik;

/**
 * Bean do obsługi kwaterowania - wypełniania tabelki Kwaterunek.
 * Robi to metoda zakwateruj() klasy Akademik.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
@Scope("session")
public class ZakwaterujBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logg = LogManager.getLogger();
	
	private transient Messagesy komunikaty;
	private transient Akademik akademik;
	
	@Autowired
	public void setAkademik(Akademik akad) {
		logg.debug("-------> setAkademik {}", akad);
		this.akademik = akad;
	}
	
	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}
	
	public Akademik getAkademik() {
		return this.akademik;
	}
	
	public void zakwateruj() {
		logg.debug("------> zakwateruj w Beanie");
		boolean ok = akademik.zakwateruj();
		komunikaty.addMessage("Kwaterowanie", ok ? "OK" : "Nie poszło");
	}

}
