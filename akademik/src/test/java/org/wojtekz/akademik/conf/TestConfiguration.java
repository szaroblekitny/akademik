package org.wojtekz.akademik.conf;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hsqldb.jdbc.JDBCDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Import(AkademikConfiguration.class)
public class TestConfiguration {
	private static Logger logg = LogManager.getLogger();
	
	/**
	 * Testowe zródło danych: baza HSQL w pamięci operacyjnej.
	 * 
	 * @return JDBCDataSource - baza testowa o nazwie app-db
	 */
	@Bean
	JDBCDataSource hsqldbDataSource() {
		logg.debug("----->>> hsqldbDataSource TEST configuration");
		JDBCDataSource source = new JDBCDataSource();
		source.setUrl("jdbc:hsqldb:mem:app-db");
		source.setUser("sa");
				
		return source;
	}
	
	/**
	 * Faktory zarządzania danymi. Skanuje pakiet org.wojtekz.akademik.entity.
	 * 
	 * @return LocalContainerEntityManagerFactoryBean dla zródła hsqldbDataSource
	 *          i adaptera Hibernate
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		logg.debug("----->>> LocalContainerEntityManagerFactoryBean TEST configuration");
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setPackagesToScan("org.wojtekz.akademik.entity");
		em.setDataSource(hsqldbDataSource());
		em.setJpaVendorAdapter(hibJpaVendorAdapter());

		return em;
	}
	
	/**
	 * Adapter JPA dostawcy Hibernate. Dialekt dla bazy HSQL.
	 * Automatyczna generacja struktur danych na podstawie definicji klas
	 * w pakiecie org.wojtekz.akademik.entity.
	 * 
	 * @return HibernateJpaVendorAdapter
	 */
	@Bean
	public HibernateJpaVendorAdapter hibJpaVendorAdapter() {
		logg.debug("----->>> HibernateJpaVendorAdapter TEST configuration");
		HibernateJpaVendorAdapter adapt = new HibernateJpaVendorAdapter();
		adapt.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");
		adapt.setGenerateDdl(true);
		
		return adapt;
	}
	
	/**
	 * Standardowy manager transakcji JPA dla testów.
	 * 
	 * @return JpaTransactionManager
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		logg.debug("----->>> PlatformTransactionManager TEST configuration");
		JpaTransactionManager transactionManager = new JpaTransactionManager();

		return transactionManager;
	}
	
}
