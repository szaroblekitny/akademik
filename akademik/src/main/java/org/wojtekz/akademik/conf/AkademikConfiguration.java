package org.wojtekz.akademik.conf;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="org.wojtekz.akademik.dao")
public class AkademikConfiguration {
	private static Logger logg = Logger.getLogger(AkademikConfiguration.class.getName());

	@Bean
	JtaTransactionManager transactionManager() {
		logg.debug("----->>> transactionManager instance ----");
		JtaTransactionManager txManager = new JtaTransactionManager();
		/*JndiTemplate template = new JndiTemplate();
		Properties jndiEnvironment = new Properties();
		jndiEnvironment.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:app-db");
		jndiEnvironment.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		jndiEnvironment.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		jndiEnvironment.setProperty("hibernate.connection.username", "sa");
		jndiEnvironment.setProperty("hibernate.connection.password", "");
		jndiEnvironment.setProperty("hibernate.generate_statistics", "true");
		jndiEnvironment.setProperty("hibernate.hbm2ddl.auto", "update");
		// jndiEnvironment.setProperty("hibernate.default_batch_fetch_size", "2");
		jndiEnvironment.setProperty("hibernate.show_sql", "true");
		jndiEnvironment.setProperty("hibernate.format_sql", "true");
		jndiEnvironment.setProperty("hibernate.connection.charSet", "UTF-8");
		
		template.setEnvironment(jndiEnvironment);
		
		txManager.setJndiTemplate(template);
		UserTransaction userTransaction = new User;
		txManager.setUserTransaction(userTransaction);*/
		
		// txManager.
		
		return txManager;
	}
	
	@Bean
	LocalEntityManagerFactoryBean entityManagerFactory() {
		logg.debug("----->>> entityManagerFactory instance ----");
		JpaDialect jpaDialect = new HibernateJpaDialect();
		JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		LocalEntityManagerFactoryBean localEntityManagerFactoryBean = new LocalEntityManagerFactoryBean();
		
		localEntityManagerFactoryBean.setJpaDialect(jpaDialect);
		localEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		localEntityManagerFactoryBean.setPersistenceUnitName("unitPU");
		return localEntityManagerFactoryBean; 
	}
	
}
