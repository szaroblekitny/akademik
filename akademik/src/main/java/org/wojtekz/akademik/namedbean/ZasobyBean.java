package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Wczytywanie opisów zasobów naszej aplikacji.
 *  
 * @author wojtek
 *
 */
@Component
@SessionScoped
public class ZasobyBean implements Serializable {
	private static final long serialVersionUID = -8413107996976351730L;
	
	private static Logger logg = Logger.getLogger(ZasobyBean.class.getName());
	
    @Autowired
	private Map<String,Object> emfProperties;
    
    public List<String> pobierzProperties() {
    	logg.debug("-----------> pobierzProperties");
    	List<String> stringProps = new ArrayList<>();
    	
    	for (Map.Entry<String,Object> entry : emfProperties.entrySet()) {
    		stringProps.add(entry.getKey() + "=" + entry.getValue());
    	}
    	
    	if (logg.isDebugEnabled()) {
    		logg.debug("-----------> " + stringProps.toString());
    	}
    	
    	return stringProps;
    }
	
}
