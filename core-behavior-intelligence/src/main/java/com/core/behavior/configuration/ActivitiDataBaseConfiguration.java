package com.core.behavior.configuration;

import com.core.behavior.properties.ActivitiProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
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
@EnableJpaRepositories(basePackages = {"com.core.activiti.repository"},
        entityManagerFactoryRef = "activitiEntityManager",
        transactionManagerRef = "activitiTransactionManager")
@Configuration
public class ActivitiDataBaseConfiguration implements EnvironmentAware {

    @Autowired
    private ActivitiProperties activitiProperties;

    private Environment environment;

    @Override
    public void setEnvironment(Environment e) {
        this.environment = e;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean activitiEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSourceActiviti());
        em.setPackagesToScan(
                new String[]{"com.core.activiti.model","com.core.activiti.repository"});

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

    @Primary
    @Bean
    public DataSource dataSourceActiviti() {

        DataSource dataSource;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setInitializationFailTimeout(-1);
        hikariConfig.setMinimumIdle(activitiProperties.getDatasource().getMinimumIdle());
        hikariConfig.setMaximumPoolSize((activitiProperties.getDatasource().getMaximumPoolSize()));
        hikariConfig.setValidationTimeout((activitiProperties.getDatasource().getValidationTimeout()));
        hikariConfig.setIdleTimeout((activitiProperties.getDatasource().getIdleTimeout()));
        hikariConfig.setConnectionTimeout((activitiProperties.getDatasource().getConnectionTimeout()));
        hikariConfig.setAutoCommit((activitiProperties.getDatasource().isAutoCommit()));
        hikariConfig.setJdbcUrl(activitiProperties.getDatasource().getSqlserver().getDataUrl());
        hikariConfig.setUsername(activitiProperties.getDatasource().getSqlserver().getDataSourceUser());
        hikariConfig.setPassword(activitiProperties.getDatasource().getSqlserver().getDataSourcePassword());
        hikariConfig.setPoolName("ActivitiPool");
        hikariConfig.setConnectionTestQuery(activitiProperties.getDatasource().getSqlserver().getConnectionTestQuery());

        dataSource = new HikariDataSource(hikariConfig);

        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager activitiTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                activitiEntityManager().getObject());
        return transactionManager;
    }

}
