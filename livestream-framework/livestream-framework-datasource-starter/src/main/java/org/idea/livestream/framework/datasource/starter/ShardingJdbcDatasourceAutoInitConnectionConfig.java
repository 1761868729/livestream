package org.idea.livestream.framework.datasource.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author dream
 * @date 2024/9/19 下午12:10
 * @description ShardingJdbcDatasourceAutoInitConnectionConfig class
 */
public class ShardingJdbcDatasourceAutoInitConnectionConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingJdbcDatasourceAutoInitConnectionConfig.class);

    @Bean
    public ApplicationRunner runner(DataSource dataSource) {
        return args -> {
            LOGGER.info("dataSource: {}", dataSource);
            Connection connection = dataSource.getConnection();
        };
    }
}
