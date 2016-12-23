package org.wojtekz.akademik.conf;

import org.apache.log4j.Logger;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebConfig extends AbstractContextLoaderInitializer {
	private static Logger logg = Logger.getLogger(WebConfig.class.getName());
	
	@Override
	protected WebApplicationContext createRootApplicationContext() {
		logg.debug("-----------> Kontekst webowy");
		AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
		webContext.register(AkademikConfiguration.class);
		return webContext;
	}

}
