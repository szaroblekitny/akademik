package org.wojtekz.akademik.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Æwiczenie dla zobaczenia jak dzia³a BeanFactoryPostProcessor.
 * @author wojtek
 *
 */
@Component
public class LoggerFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static Logger logg = LogManager.getLogger();
	
	public LoggerFactoryPostProcessor() {
		logg.debug("-----------> konstruktor LoggerFactoryPostProcessor");
	}

	/**
	 * Tylko loguje nazwê BeanClassLoadera.
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
		
		if (logg.isDebugEnabled()) {
			logg.debug("-----------> postProcessBeanFactory moze modyfikowac kontekst dla faktory " + factory.getClass().getName());
			
			logg.debug("------------------------> Beany:");
			String[] listaBeanow = factory.getBeanDefinitionNames();
			for (int ii = 0; ii < listaBeanow.length; ii++) {
				logg.debug("***** " + listaBeanow[ii]);
			}
			
			if (factory.getBeanClassLoader() != null) {
				logg.debug("-------------------------------------> BeanClassLoader: " + factory.getBeanClassLoader().getClass().getName());
			}
		}
		
	}

}
