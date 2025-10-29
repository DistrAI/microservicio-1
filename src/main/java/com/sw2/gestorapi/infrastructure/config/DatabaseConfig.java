package com.sw2.gestorapi.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Bean
    @Profile("prod")
    public DataSource productionDataSource() {
        log.info("Configurando DataSource para producción");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(databaseUsername);
        config.setPassword(databasePassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración optimizada para producción
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutos
        config.setMaxLifetime(1200000); // 20 minutos
        config.setConnectionTimeout(20000); // 20 segundos
        config.setLeakDetectionThreshold(60000); // 1 minuto
        
        // Configuraciones adicionales
        config.setPoolName("DistrIA-HikariCP");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        // Health check
        config.setConnectionTestQuery("SELECT 1");
        
        return new HikariDataSource(config);
    }

    @Bean
    @Profile("!prod")
    public DataSource developmentDataSource() {
        log.info("Configurando DataSource para desarrollo");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(databaseUsername);
        config.setPassword(databasePassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración para desarrollo
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(600000); // 10 minutos
        config.setMaxLifetime(1800000); // 30 minutos
        config.setConnectionTimeout(30000); // 30 segundos
        
        config.setPoolName("DistrIA-Dev-HikariCP");
        config.setConnectionTestQuery("SELECT 1");
        
        return new HikariDataSource(config);
    }
}
