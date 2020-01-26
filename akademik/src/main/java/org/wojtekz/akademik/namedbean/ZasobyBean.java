package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.core.WlasciwosciPersystencji;

/**
 * Wczytywanie opis�w zasob�w aplikacji.
 *  
 * @author wojtek
 *
 */
@Component
@SessionScoped
public class ZasobyBean implements Serializable {
	private static final long serialVersionUID = -8799560567435187001L;

	private static Logger logg = LogManager.getLogger();
	
    @Autowired
	private WlasciwosciPersystencji wp;
    
    /**
     * U�ywa klasy WlasciwosciPersystencji do pobrania w�a�ciwo�ci z EntityManagerFactory
     * i zwraca j� w formie listy prostej do wy�wietlenia.
     * 
     * @return lista w�a�ciwo�ci EntityManagerFactory zwi�zanego z aplikacj�.
     */
    public List<String> pobierzProperties() {
    	logg.debug("-----------> pobierzProperties");
    	
    	List<String> lista = wp.listaWlasciwosci();
    	
    	if (logg.isDebugEnabled()) {
    		logg.debug("-----------> lista: " + lista.toString());
    	}
    	
    	return lista;
    }
	
}
