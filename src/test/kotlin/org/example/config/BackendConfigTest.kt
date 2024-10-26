package org.example.config

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.example.assertj.spring.ApplicationContextAssertions
import org.example.model.Country
import org.example.service.CountryDataJpaService
import org.example.service.CountryService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

internal class BackendConfigTest {

    @Test
    fun countBeans() {
        val beanDefinitionCount = ctx.beanDefinitionCount
        assertThat(beanDefinitionCount).isGreaterThan(10)
    }

    @Test
    fun listBeans() {
        val beanDefinitionNames = ctx.beanDefinitionNames
        assertThat(beanDefinitionNames)
            .hasSizeGreaterThan(10)
            .contains("entityManagerBean", "entityManagerFactoryBean", "dataSource", "txManager")
            .contains("countryService", "countryRepositoryHibernate", "countryRepositoryJpa")

        // Assertions directly on ApplicationContext
        ApplicationContextAssertions.assertThat(ctx)
            .hasBean("entityManagerBean")
            .hasExactlyOneBeanOfType(EntityManagerFactory::class.java)
            .hasBeansOfType(EntityManager::class.java)
    }

    @Test
    fun checkBeanDataSourceOfGivenType() {
        // Assertions directly on ApplicationContext
        ApplicationContextAssertions.assertThat(ctx)
            .isBeanOfType("dataSource", DataSource::class.java)
    }

    @Test
    @Throws(SQLException::class)
    fun checkDataSource_getConnection() {
        val ds: DataSource = ctx.getBean(DataSource::class.java)
        val cnx: Connection = ds.connection
        assertThat(cnx.isClosed).isFalse()
        assertThat(cnx.catalog).isEqualTo("world-db")
        assertThat(cnx.schema).isEqualTo("public")
    }

    @Test
    @Disabled("Needs local database -- see IT")
    fun serviceRead() {
        val all: List<Country?> = countryService.findAll()
        log.info("Nb countries = {}", all.size)
        assertThat(all).hasSizeGreaterThan(100)
    }

    @Test
    @Disabled("Needs local database -- see IT")
    fun serviceDataJpaRead() {
        val all: List<Country?> = countryDataJpaService.findAll()
        log.info("Nb countries = {}", all.size)
        assertThat(all).hasSizeGreaterThan(100)
    }

    @Test
    @Disabled("Needs local database -- see IT")
    fun serviceWrite() {
        val newCountry = Country(
            code = "MRS",
            code2 = "M_",
            name = "Mars",
            localName = "👽",
            continent = "Antarctica",
            region = "Space",
            surfaceArea = 15200000.0,
            governmentForm = "Freedom",
            population = 999999999L,
            lifeExpectancy = 1556.5
        )
        countryService.save(newCountry)

        val persistedEntity: Country? = countryService.findById("MRS")
        assertThat(persistedEntity).isNotNull
    }

    @Test
    @Disabled("Needs local database -- see IT")
    fun serviceRemove() {
        countryService.deleteById("MRS")

        val persistedEntity: Country? = countryService.findById("MRS")
        assertThat(persistedEntity).isNotNull
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(BackendConfigTest::class.java)

        private lateinit var ctx: ApplicationContext

        private lateinit var countryService: CountryService
        private lateinit var countryDataJpaService: CountryDataJpaService

        @JvmStatic
        @BeforeAll
        fun setUpGlobal() {
            ctx = AnnotationConfigApplicationContext(BackendConfig::class.java)
            countryService = ctx.getBean(CountryService::class.java)
            countryDataJpaService = ctx.getBean(CountryDataJpaService::class.java)
        }
    }
}