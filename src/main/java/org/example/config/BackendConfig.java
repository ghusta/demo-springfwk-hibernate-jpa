package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.tool.schema.Action;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"org.example.persistence", "org.example.service"})
@EnableTransactionManagement
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class BackendConfig {

    @Value("${application.name:#{null}}")
    private Optional<String> appName;

    // Using relaxed binding for property names
    // can also read env var 'JDBC_URL'
    @Value("${jdbc.url:jdbc:postgresql://localhost:5432/world-db}")
    private String propJdbcUrl;

    @Value("${jdbc.username:world}")
    private String propJdbcUsername;

    @Value("${jdbc.password:world123}")
    private String propJdbcPassword;

    // Needed to resolve ${} placeholders in @Value annotations
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(propJdbcUrl);
        dataSource.setUsername(propJdbcUsername);
        dataSource.setPassword(propJdbcPassword);
        // See: https://stackoverflow.com/questions/1911253/the-infamous-java-sql-sqlexception-no-suitable-driver-found
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager(EntityManagerFactory emf) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(emf);
        // jpaTransactionManager.setJpaDialect(new HibernateJpaDialect());
        return jpaTransactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("org.example.model");

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(AvailableSettings.DIALECT, PostgreSQL10Dialect.class.getName());
        hibernateProperties.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.NONE.getExternalHbm2ddlName());
        entityManagerFactoryBean.setJpaProperties(hibernateProperties);

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        return entityManagerFactoryBean;
    }

    @Bean
    public SharedEntityManagerBean entityManagerBean(EntityManagerFactory emf) {
        SharedEntityManagerBean sharedEntityManagerBean = new SharedEntityManagerBean();
        sharedEntityManagerBean.setEntityManagerFactory(emf);
        return sharedEntityManagerBean;
    }

}
