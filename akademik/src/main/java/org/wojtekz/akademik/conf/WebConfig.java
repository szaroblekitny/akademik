package org.wojtekz.akademik.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebConfig extends AbstractContextLoaderInitializer {
	private static Logger logg = LogManager.getLogger();
	
	public WebConfig() {
		logg.trace("-----------> konstruktor WebConfig");
	}
	
	@Override
	protected WebApplicationContext createRootApplicationContext() {
		logg.debug("-----------> Kontekst webowy");
		AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
		webContext.register(MainConfiguration.class);
		return webContext;
	}

}
