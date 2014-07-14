package org.wojtekz.akademik.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wojtekz.akademik.dao.StudentDao;
import org.wojtekz.akademik.dao.StudentDaoImpl;

@Configuration
public class AkademikConfiguration {
	@Bean
	StudentDao studentDao() {
		return new StudentDaoImpl();
	}
	
}
