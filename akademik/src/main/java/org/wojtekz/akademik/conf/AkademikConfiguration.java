package org.wojtekz.akademik.conf;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.wojtekz.akademik.core.PokojService;
import org.wojtekz.akademik.core.PokojServiceImpl;
import org.wojtekz.akademik.core.StudentService;
import org.wojtekz.akademik.core.StudentServiceImpl;

@Configuration
@ComponentScan(basePackages={"org.wojtekz.akademik.dao", "org.wojtekz.akademik.core"})
@ImportResource("classpath:config_dao.xml")
public class AkademikConfiguration {
	private static Logger logg = Logger.getLogger(AkademikConfiguration.class.getName());
	
	@Bean
	PokojService pokojService() {
		logg.debug("----->>> pokojService bean configuration");
		return new PokojServiceImpl();
	}
	
	@Bean
	StudentService studentService() {
		logg.debug("----->>> studentService bean configuration");
		return new StudentServiceImpl();
	}
}
