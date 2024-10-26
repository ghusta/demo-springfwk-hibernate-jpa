package org.example.service

import org.assertj.core.api.Assertions.assertThat
import org.example.config.BackendConfig
import org.example.model.Country
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.sql.DatabaseMetaData
import java.sql.SQLException
import javax.sql.DataSource

@SpringJUnitConfig(BackendConfig::class)
@Testcontainers
internal class CountryServiceIT {

    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var countryService: CountryService

    @Test
    @Throws(SQLException::class)
    fun databaseMetaData() {
        val databaseMetaData: DatabaseMetaData = dataSource.connection.metaData

        val databaseMajorVersion: Int = databaseMetaData.databaseMajorVersion
        assertThat(databaseMajorVersion).isEqualTo(17)

        val databaseProductName: String? = databaseMetaData.databaseProductName
        assertThat(databaseProductName).containsIgnoringCase("postgresql")

        val driverName: String? = databaseMetaData.driverName
        assertThat(driverName).isEqualTo("PostgreSQL JDBC Driver")
    }

    @Test
    fun serviceRead() {
        val all: List<Country?> = countryService.findAll()
        log.info("Nb countries = {}", all.size)
        assertThat(all).hasSizeGreaterThan(100)
    }

    @Test
    fun serviceFindCountryFrance() {
        val byId: Country? = countryService.findById("FRA")
        assertThat(byId).isNotNull()
        assertThat(byId?.name).isEqualTo("France")
    }

    @Test
    @Transactional
    @Rollback
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
        assertThat(persistedEntity).isNotNull()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CountryServiceIT::class.java)

        @Container
        var postgresWorldDB = PostgreSQLContainer(
            DockerImageName.parse("ghusta/postgres-world-db:2.12").asCompatibleSubstituteFor("postgres")
        )
            .withDatabaseName("world-db")
            .withUsername("world")
            .withPassword("world123")

        @DynamicPropertySource
        fun jdbcProperties(registry: DynamicPropertyRegistry) {
            registry.add("jdbc.url") { postgresWorldDB.jdbcUrl }
            registry.add(
                "jdbc.username"
            ) { postgresWorldDB.username }
            registry.add(
                "jdbc.password"
            ) { postgresWorldDB.password }

            registry.add(
                "unused.jdbc.host"
            ) { postgresWorldDB.host }
            registry.add(
                "unused.jdbc.port"
            ) { postgresWorldDB.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT) }
        }
    }
}