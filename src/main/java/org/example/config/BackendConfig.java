package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"org.example.persistence", "org.example.service", "org.example.mapper"})
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.example.persistence.data.jpa",
        entityManagerFactoryRef = "entityManagerFactoryBean", transactionManagerRef = "txManager")
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

    private final Environment env;

    public BackendConfig(Environment env) {
        this.env = env;
    }

    /**
     * Needed to resolve ${...} placeholders in {@link Value @Value} annotations or through {@link Environment}.
     *
     * @see PropertySource
     */
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

    /**
     * May be injected later with {@link jakarta.persistence.PersistenceUnit @PersistenceUnit}.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("org.example.model");

        Properties hibernateProperties = new Properties();
        // org.hibernate.orm.deprecation - HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
        // hibernateProperties.setProperty(AvailableSettings.DIALECT, PostgreSQLDialect.class.getName());
        hibernateProperties.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.NONE.getExternalHbm2ddlName());
        entityManagerFactoryBean.setJpaProperties(hibernateProperties);

        AbstractJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        // jpaVendorAdapter.setShowSql(true);
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        return entityManagerFactoryBean;
    }

    /**
     * May be injected later with {@link jakarta.persistence.PersistenceContext @PersistenceContext}.
     */
    @Bean
    public SharedEntityManagerBean entityManagerBean(EntityManagerFactory emf) {
        SharedEntityManagerBean sharedEntityManagerBean = new SharedEntityManagerBean();
        sharedEntityManagerBean.setEntityManagerFactory(emf);
        return sharedEntityManagerBean;
    }

}
