package org.wojtekz.akademik.conf;

import org.apache.log4j.Logger;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebConfig extends AbstractContextLoaderInitializer {
	private static Logger logg = Logger.getLogger(WebConfig.class.getName());
	
	public WebConfig() {
		logg.debug("-----------> konstruktor WebConfig");
	}
	
	@Override
	protected WebApplicationContext createRootApplicationContext() {
		logg.debug("-----------> Kontekst webowy");
		AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
		webContext.register(MainConfiguration.class);
		return webContext;
	}

}
