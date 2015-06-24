package gov.gtas.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * The configuration class can be imported into an XML configuration by:<br>
 * <context:annotation-config/>
 * <bean class="gov.gtas.config.CommonServicesConfig"/>
 * 
 * @author GTAS4
 *
 */

@Configuration
@ComponentScan("gov.gtas")
@PropertySource({"classpath:commonservices.properties", "classpath:hibernate.properties"})
@EnableJpaRepositories("gov.gtas")
@EnableTransactionManagement
public class CommonServicesConfig {
	
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "hibernate.connection.driver_class";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "hibernate.connection.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "hibernate.connection.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "hibernate.connection.username";
    
    private static final String PROPERTY_NAME_SECOND_LEVEL_CACHE="hibernate.cache.use_second_level_cache";
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
    private static final String PROPERTY_NAME_HIBERNATE_CACHE = "hibernate.cache";
    private static final String PROPERTY_NAME_HIBERNATE_QUERY_CACHE = "hibernate.cache.use_query_cache";
    private static final String PROPERTY_NAME_HIBERNATE_STATS = "hibernate.statistics";
    private static final String PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER = "hibernate.cache.provider_class";
    private static final String PROPERTY_NAME_HIBERNATE_CACHE_FACTORY = "hibernate.cache.region.factory_class";
    private static final String PROPERTY_NAME_SRC_EH_ROOT="hibernate.cache.provider_configuration_file_resource_path";
    
    @Resource
    private Environment env;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
 
        dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        dataSource.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        dataSource.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
 
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
         
        entityManagerFactoryBean.setJpaProperties(hibProperties());
         
        return entityManagerFactoryBean;
    }
 
    private Properties hibProperties() {
        Properties properties = new Properties();
        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        properties.put(PROPERTY_NAME_HIBERNATE_QUERY_CACHE,env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_QUERY_CACHE));
        properties.put(PROPERTY_NAME_SECOND_LEVEL_CACHE,env.getRequiredProperty(PROPERTY_NAME_SECOND_LEVEL_CACHE));
        properties.put(PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER,env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER));
        properties.put(PROPERTY_NAME_HIBERNATE_CACHE_FACTORY,env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_FACTORY));
        properties.put(PROPERTY_NAME_SRC_EH_ROOT,env.getRequiredProperty(PROPERTY_NAME_SRC_EH_ROOT));

        return properties;
    }
    
    
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
