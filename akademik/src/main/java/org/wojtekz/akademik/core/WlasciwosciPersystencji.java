package org.wojtekz.akademik.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Opakowanie dla w³aœciwoœci LocalContainerEntityManagerFactoryBean. S¹ one zwracane
 * przez metodê getJpaPropertyMap() jako Map. To dosyæ bruŸdzi, bo jeœli zrobiæ
 * beana o typie Map, to sporo innych próbuje go do³¹czyæ z komunikatem
 * "Autowiring by type from bean name 'xxxXXX' to bean named 'yyyYYY'"
 * 
 * @author wojtek
 *
 */
public class WlasciwosciPersystencji implements Ordered {
	private static Logger logg = Logger.getLogger(WlasciwosciPersystencji.class.getName());
	
	private Map<String, Object> wlasciwosci;
	
	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManagerFactory;
	
	public WlasciwosciPersystencji() {
		logg.debug("-----------> konstruktor WlasciwosciPersystencji");
	}
	
	public void init() {
		this.wlasciwosci = entityManagerFactory.getJpaPropertyMap();
	}
	
	public List<String> listaWlasciwosci() {
		List<String> stringProps = new ArrayList<>();
    	
    	for (Map.Entry<String, Object> entry : wlasciwosci.entrySet()) {
    		stringProps.add(entry.getKey() + "<==>" + entry.getValue());
    	}
    	
    	if (logg.isDebugEnabled() && stringProps.isEmpty()) {
    		logg.debug("-----------> wlasciwosci puste");
    	}
    	
    	return stringProps;
	}

	public Map<String, Object> getWlasciwosci() {
		return wlasciwosci;
	}

	public void setWlasciwosci(Map<String, Object> wlasciwosci) {
		this.wlasciwosci = wlasciwosci;
	}

	@Override
	public String toString() {
		return "WlasciwosciPersystencji: " + listaWlasciwosci().toString();
	}

	@Override
	public int getOrder() {
		logg.debug("-----------> WlasciwosciPersystencji order");
		return Ordered.LOWEST_PRECEDENCE;
	}
	
}
