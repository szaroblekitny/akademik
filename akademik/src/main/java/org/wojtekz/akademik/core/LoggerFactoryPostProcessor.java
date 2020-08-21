package org.wojtekz.akademik.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Component;

/**
 * Ćwiczenie dla zobaczenia jak działa BeanFactoryPostProcessor.
 * @author Wojciech Zaręba
 *
 */
@Component
public class LoggerFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static Logger logg = LogManager.getLogger();
	
	public LoggerFactoryPostProcessor() {
		logg.trace("-------> konstruktor LoggerFactoryPostProcessor");
	}

	/**
	 * Tylko loguje nazwę BeanClassLoadera i jako trace spis beanów.
	 * Jako dodatkowy bonus: wersja Javy (JDK) i Springa.
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) {
		logg.info("-------> Wersja JDK {}", System.getProperty("java.version"));
		logg.info("-------> Wersja Springa {}", SpringVersion.getVersion());
		
		if (logg.isDebugEnabled()) {
			logg.debug("-----------> postProcessBeanFactory moze modyfikowac kontekst dla faktory {}", factory.getClass().getName());
			
			if (logg.isTraceEnabled()) {
				logg.trace("-------------> Beany:");
				String[] listaBeanow = factory.getBeanDefinitionNames();
				for (int ii = 0; ii < listaBeanow.length; ii++) {
					logg.trace("***** {}", listaBeanow[ii]);
				}
			}
			
			if (factory.getBeanClassLoader() != null) {
				logg.debug("-----------> BeanClassLoader: {}", factory.getBeanClassLoader().getClass().getName());
			}
		}
		
	}

}
