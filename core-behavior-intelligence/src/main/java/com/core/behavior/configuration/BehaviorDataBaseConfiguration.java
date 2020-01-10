package com.core.behavior.configuration;

import com.core.behavior.properties.BehaviorProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    
    @org.springframework.beans.factory.annotation.Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void setEnvironment(Environment e) {
        this.environment = e;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean behaviorEntityManager() {
        LocalContainerEntityManagerFactoryBean em
          = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSourceBehavior());
        em.setPackagesToScan(
          new String[] { "com.core.behavior.model", "com.core.behavior.repository" });
 
        System.out.println("hibernate.dialect" +  environment.getProperty("hibernate.dialect"));
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
          environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
          "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);
 
        return em;
    }
    
    @Primary
    @Bean
    public DataSource dataSourceBehavior() {

        DataSource dataSource;

        
        Properties props = new Properties();
//        props.setProperty("dataSource.cachePrepStmts", "true");
//        props.setProperty("dataSource.prepStmtCacheSize", "250");
//        props.setProperty("dataSource.prepStmtCacheSqlLimit", "2048");
//        props.setProperty("dataSource.useServerPrepStmts", "true");
//        props.setProperty("dataSource.useLocalSessionState", "true");
//        props.setProperty("dataSource.rewriteBatchedStatements", "true");
//        props.setProperty("dataSource.cacheResultSetMetadata", "true");
//        props.setProperty("dataSource.cacheServerConfiguration", "true");
//        props.setProperty("dataSource.cacheServerConfiguration", "true");
        
        
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
        
        Logger.getLogger(BehaviorDataBaseConfiguration.class.getName()).log(Level.INFO, "Profile -> " + activeProfile );

        return dataSource;
    }
    
    @Primary
    @Bean
    public PlatformTransactionManager behaviorTransactionManager() {
  
        JpaTransactionManager transactionManager
          = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
          behaviorEntityManager().getObject());
        return transactionManager;
    }

}
