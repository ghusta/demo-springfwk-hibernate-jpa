package org.example.config

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import org.hibernate.cfg.AvailableSettings
import org.hibernate.tool.schema.Action
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.support.SharedEntityManagerBean
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = ["org.example.persistence", "org.example.service"])
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["org.example.persistence.data.jpa"],
    entityManagerFactoryRef = "entityManagerFactoryBean",
    transactionManagerRef = "txManager"
)
@PropertySource(value = ["classpath:application.properties"], ignoreResourceNotFound = true)
class BackendConfig(
    private val env: Environment,
    @Value("\${application.name:#{null}}") val appName: String?,
    /**
     * Using relaxed binding for property names,
     * can also read env var 'JDBC_URL'.
     */
    @Value("\${jdbc.url:jdbc:postgresql://localhost:5432/world-db}") val propJdbcUrl: String,
    @Value("\${jdbc.username:world}") val propJdbcUsername: String,
    @Value("\${jdbc.password:world123}") val propJdbcPassword: String
) {

    @Bean
    fun dataSource(): DataSource {
        return HikariDataSource().apply {
            jdbcUrl = propJdbcUrl
            username = propJdbcUsername
            password = propJdbcPassword
            // See: https://stackoverflow.com/questions/1911253/the-infamous-java-sql-sqlexception-no-suitable-driver-found
            driverClassName = "org.postgresql.Driver"
        }
    }

    @Bean
    fun txManager(emf: EntityManagerFactory): PlatformTransactionManager {
        val jpaTransactionManager = JpaTransactionManager(emf)
        // jpaTransactionManager.setJpaDialect(new HibernateJpaDialect());
        return jpaTransactionManager
    }

    @Bean
    fun entityManagerFactoryBean(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.dataSource = dataSource
        entityManagerFactoryBean.setPackagesToScan("org.example.model")

        val hibernateProperties = Properties()
        // org.hibernate.orm.deprecation - HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
        // hibernateProperties.setProperty(AvailableSettings.DIALECT, PostgreSQLDialect.class.getName());
        hibernateProperties.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.NONE.externalHbm2ddlName)
        entityManagerFactoryBean.setJpaProperties(hibernateProperties)

        val jpaVendorAdapter: AbstractJpaVendorAdapter = HibernateJpaVendorAdapter()
        // jpaVendorAdapter.setShowSql(true);
        entityManagerFactoryBean.jpaVendorAdapter = jpaVendorAdapter
        return entityManagerFactoryBean
    }

    @Bean
    fun entityManagerBean(emf: EntityManagerFactory): SharedEntityManagerBean {
        val sharedEntityManagerBean = SharedEntityManagerBean()
        sharedEntityManagerBean.entityManagerFactory = emf
        return sharedEntityManagerBean
    }

    companion object {
        /**
         * Needed to resolve ${} placeholders in [@Value][Value] annotations or through [Environment].
         *
         * @see PropertySource
         */
        @Bean
        fun propertyConfig(): PropertySourcesPlaceholderConfigurer {
            return PropertySourcesPlaceholderConfigurer()
        }
    }
}
