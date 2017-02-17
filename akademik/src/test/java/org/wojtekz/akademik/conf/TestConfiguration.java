package org.wojtekz.akademik.conf;

import org.apache.log4j.Logger;
import org.hsqldb.jdbc.JDBCDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.wojtekz.akademik.util.DaneTestowe;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("org.wojtekz.akademik.repos")
@Import(AkademikConfiguration.class)
public class TestConfiguration {
	private static Logger logg = Logger.getLogger(TestConfiguration.class.getName());
	
	@Bean
	JDBCDataSource hsqldbDataSource() {
		logg.debug("----->>> hsqldbDataSource TEST configuration");
		JDBCDataSource source = new JDBCDataSource();
		source.setUrl("jdbc:hsqldb:mem:app-db");
		source.setUser("sa");
				
		return source;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		logg.debug("----->>> LocalContainerEntityManagerFactoryBean TEST configuration");
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setPackagesToScan("org.wojtekz.akademik.entity");
		em.setDataSource(hsqldbDataSource());
		em.setJpaVendorAdapter(hibJpaVendorAdapter());

		return em;
	}
	
	@Bean
	public HibernateJpaVendorAdapter hibJpaVendorAdapter() {
		HibernateJpaVendorAdapter adapt = new HibernateJpaVendorAdapter();
		adapt.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");
		adapt.setGenerateDdl(true);
		
		return adapt;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		logg.debug("----->>> PlatformTransactionManager TEST configuration");
		JpaTransactionManager transactionManager = new JpaTransactionManager();

		return transactionManager;
	}
	
}
