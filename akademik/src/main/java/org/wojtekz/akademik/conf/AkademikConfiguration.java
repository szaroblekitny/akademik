package org.wojtekz.akademik.conf;

// import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
// import org.wojtekz.akademik.dao.StudentDao;
// import org.wojtekz.akademik.dao.StudentDaoImpl;

@Configuration
@ComponentScan(basePackages="org.wojtekz.akademik.dao")
@ImportResource("classpath:config_dao.xml")
public class AkademikConfiguration {
	// private static Logger logg = Logger.getLogger(AkademikConfiguration.class.getName());
	
	/*@Bean
	StudentDao studentDao() {
		return new StudentDaoImpl();
	}*/

	/*@Bean
	LocalEntityManagerFactoryBean entityManagerFactory() {
		logg.debug("----->>> entityManagerFactory instance ----");
		JpaDialect jpaDialect = new HibernateJpaDialect();
		JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		LocalEntityManagerFactoryBean localEntityManagerFactoryBean = new LocalEntityManagerFactoryBean();
		
		localEntityManagerFactoryBean.setJpaDialect(jpaDialect);
		localEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		localEntityManagerFactoryBean.setPersistenceUnitName("unitPU");
		return localEntityManagerFactoryBean; 
	}*/
	
}
