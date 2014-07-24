package org.wojtekz.akademik.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
// import org.wojtekz.akademik.dao.StudentDao;
// import org.wojtekz.akademik.dao.StudentDaoImpl;

@Configuration
@ComponentScan(basePackages="org.wojtekz.akademik.dao")
public class AkademikConfiguration {
	/*@Bean
	StudentDao studentDao() {
		return new StudentDaoImpl();
	}*/
	
	@Bean
	LocalEntityManagerFactoryBean entityManagerFactory() {
		JpaDialect jpaDialect = new HibernateJpaDialect();
		JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		LocalEntityManagerFactoryBean localEntityManagerFactoryBean = new LocalEntityManagerFactoryBean();
		
		localEntityManagerFactoryBean.setJpaDialect(jpaDialect);
		localEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		localEntityManagerFactoryBean.setPersistenceUnitName("unitPU");
		return localEntityManagerFactoryBean; 
	}
	
}
