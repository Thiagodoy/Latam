package com.core.behavior.configuration;

import com.core.behavior.properties.BehaviorProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@EnableJpaRepositories(basePackages = {"com.core.behavior.repository"},
         entityManagerFactoryRef = "behaviorEntityManager", 
        transactionManagerRef = "behaviorTransactionManager") 
@Configuration
public class BehaviorDataBaseConfiguration implements EnvironmentAware {

    @Autowired
    private BehaviorProperties behaviorProperties;

    private Environment environment;

    @Override
    public void setEnvironment(Environment e) {
        this.environment = e;
    }

    
    @Bean
    public LocalContainerEntityManagerFactoryBean behaviorEntityManager() {
        LocalContainerEntityManagerFactoryBean em
          = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSourceBehavior());
        em.setPackagesToScan(
          new String[] { "com.core.behavior.model", "com.core.behavior.repository" });
 
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
          environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
          environment.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);
 
        return em;
    }
    
    @Bean
    public DataSource dataSourceBehavior() {

        DataSource dataSource;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setInitializationFailTimeout(-1);
        hikariConfig.setMinimumIdle(behaviorProperties.getDatasource().getMinimumIdle());
        hikariConfig.setMaximumPoolSize((behaviorProperties.getDatasource().getMaximumPoolSize()));
        hikariConfig.setValidationTimeout((behaviorProperties.getDatasource().getValidationTimeout()));
        hikariConfig.setIdleTimeout((behaviorProperties.getDatasource().getIdleTimeout()));
        hikariConfig.setConnectionTimeout((behaviorProperties.getDatasource().getConnectionTimeout()));
        hikariConfig.setAutoCommit((behaviorProperties.getDatasource().isAutoCommit()));        
        hikariConfig.setJdbcUrl(behaviorProperties.getDatasource().getSqlserver().getDataUrl());
        hikariConfig.setUsername(behaviorProperties.getDatasource().getSqlserver().getDataSourceUser());
        hikariConfig.setPassword(behaviorProperties.getDatasource().getSqlserver().getDataSourcePassword());
        hikariConfig.setPoolName("BehaviorPool");
        hikariConfig.setConnectionTestQuery(behaviorProperties.getDatasource().getSqlserver().getConnectionTestQuery());

        dataSource = new HikariDataSource(hikariConfig);

        return dataSource;
    }
    
     @Bean
    public PlatformTransactionManager behaviorTransactionManager() {
  
        JpaTransactionManager transactionManager
          = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
          behaviorEntityManager().getObject());
        return transactionManager;
    }

}
