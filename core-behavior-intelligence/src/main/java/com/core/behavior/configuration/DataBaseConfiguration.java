package com.core.behavior.configuration;

import com.core.behavior.properties.BehaviorProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
public class DataBaseConfiguration implements EnvironmentAware {

    @Autowired
    private BehaviorProperties behaviorProperties;

    private Environment environment;

    @Override
    public void setEnvironment(Environment e) {
        this.environment = e;
    }

    @Bean
    public DataSource dataSource() {

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

}
