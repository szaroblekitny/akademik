package org.wojtekz.akademik.core;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Ta klasa jest �wiczeniem implementacji interfejsu BeanPostProcessor.
 * Chwilowo tylko sobie patrz�, jak dzia�a ten mechanizm. Po prostu wpisuj� do logu
 * punkty inicjalizacji bean�w.
 * 
 * @author wojtek
 *
 */
@Component
public class LoggerPostProcessor implements BeanPostProcessor, Ordered {
	private static Logger logg = Logger.getLogger(LoggerPostProcessor.class.getName());

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
