package org.wojtekz.akademik.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.spi.PersistenceUnitInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Opakowanie dla właściwości LocalContainerEntityManagerFactoryBean. Są one zwracane
 * przez metodę getJpaPropertyMap() jako Map. To dosyć bruździ, bo jeśli zrobić
 * beana o typie Map, to sporo innych próbuje go dołączyć z komunikatem
 * "Autowiring by type from bean name 'xxxXXX' to bean named 'yyyYYY'"
 * 
 * @author Wojtek Zaręba
 *
 */
public class WlasciwosciPersystencji implements Ordered {
	private static Logger logg = LogManager.getLogger();
	
	private Map<String, Object> wlasciwosciMap;
	private PersistenceUnitInfo prsisInfo;
	
	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManagerFactory;
	
	public WlasciwosciPersystencji() {
		logg.debug("-----------> konstruktor WlasciwosciPersystencji");
	}
	
	public void init() {
		logg.debug("-----------> WlasciwosciPersystencji init");
	}
	
	public List<String> listaWlasciwosci() {
		List<String> stringProps = new ArrayList<>();
		
		this.wlasciwosciMap = entityManagerFactory.getJpaPropertyMap();
		this.prsisInfo = entityManagerFactory.getPersistenceUnitInfo();
		
		if (this.wlasciwosciMap.isEmpty()) {
			logg.debug("-----------> JpaPropertyMap pusta");
		}
		
		if (this.prsisInfo.getProperties().isEmpty() ) {
			logg.debug("-----------> wlasciwosciMap PersistenceUnitInfo puste");
		}
		
		if (logg.isDebugEnabled()) {
			logg.debug("-----------> Unit name: " + this.prsisInfo.getPersistenceUnitName());
		}
    	
    	for (Map.Entry<String, Object> entry : wlasciwosciMap.entrySet()) {
    		stringProps.add(entry.getKey() + "<==>" + entry.getValue());
    	}
    	
    	if (stringProps.isEmpty()) {
    		logg.debug("-----------> wlasciwosci puste");
    	}
    	
    	return stringProps;
	}

	public Map<String, Object> getWlasciwosci() {
		return wlasciwosciMap;
	}

	public void setWlasciwosci(Map<String, Object> wlasciwosci) {
		this.wlasciwosciMap = wlasciwosci;
	}

	@Override
	public String toString() {
		return "WlasciwosciPersystencji: " + listaWlasciwosci().toString();
	}
	
	
	public PersistenceUnitInfo getPrsisInfo() {
		return prsisInfo;
	}

	public void setPrsisInfo(PersistenceUnitInfo prsisInfo) {
		this.prsisInfo = prsisInfo;
	}

	@Override
	public int getOrder() {
		logg.debug("-----------> WlasciwosciPersystencji order");
		return Ordered.LOWEST_PRECEDENCE;
	}
	
}
