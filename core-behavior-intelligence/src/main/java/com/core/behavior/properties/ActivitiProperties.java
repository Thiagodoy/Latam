
package com.core.behavior.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@ConfigurationProperties(prefix = "activiti", ignoreUnknownFields = true)
public class ActivitiProperties {    

    @Getter
    private final DataSource datasource = new DataSource();    

    
    public static class DataSource {

        @Getter
        @Setter
        private Integer socketTimeout;

        @Getter
        @Setter
        private Integer loginTimeout;

        @Getter
        @Setter
        private Integer minimumIdle;

        @Getter
        @Setter
        private Integer maximumPoolSize;

        @Getter
        @Setter
        private Long validationTimeout;

        @Getter
        @Setter
        private Long idleTimeout;

        @Getter
        @Setter
        private Long connectionTimeout;

        @Getter
        @Setter
        private boolean autoCommit;

        @Getter
        @Setter
        private boolean initializationFailFast;

        @Getter
        @Setter
        private Sqlserver sqlserver = new Sqlserver();

        public static class Sqlserver {

            @Getter
            @Setter
            private String dataUrl;

            @Getter
            @Setter
            private String dataSourceClassName;

            @Getter
            @Setter
            private int port;

            @Getter
            @Setter
            private String connectionTestQuery;

            @Getter
            @Setter
            private String dataSourceUser;

            @Getter
            @Setter
            private String dataSourcePassword;

        }

    }    
}
