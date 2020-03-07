package org.wojtekz.akademik.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Ta klasa jest ćwiczeniem implementacji interfejsu BeanPostProcessor.
 * Chwilowo tylko sobie patrzę, jak działa ten mechanizm. Po prostu wpisuję do logu
 * punkty inicjalizacji beanów.
 * 
 * @author wojtek
 *
 */
@Component
public class LoggerPostProcessor implements BeanPostProcessor, Ordered {
	private static Logger logg = LogManager.getLogger();

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		logg.debug("-----------> postProcessBeforeInitialization for bean " + beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		logg.debug("-----------> AfterInitialization bean '" + beanName + "' created : " + bean.toString());
		return bean;
	}


}
