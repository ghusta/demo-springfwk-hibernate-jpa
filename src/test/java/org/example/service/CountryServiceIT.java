package org.example.service;

import org.example.config.BackendConfig;
import org.example.model.Country;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = BackendConfig.class)
@Testcontainers
class CountryServiceIT {

    private static final Logger log = LoggerFactory.getLogger(CountryServiceIT.class);
    @Container
    static PostgreSQLContainer<?> postgresWorldDB = new PostgreSQLContainer<>(
            DockerImageName.parse("ghusta/postgres-world-db:2.12").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("world-db")
            .withUsername("world")
            .withPassword("world123");

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CountryService countryService;

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("jdbc.url", () -> postgresWorldDB.getJdbcUrl());
        registry.add("jdbc.username", () -> postgresWorldDB.getUsername());
        registry.add("jdbc.password", () -> postgresWorldDB.getPassword());

        registry.add("unused.jdbc.host", () -> postgresWorldDB.getHost());
        registry.add("unused.jdbc.port", () -> postgresWorldDB.getMappedPort(5432));
    }

    @Test
    void databaseMetaData() throws SQLException {
        DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();

        int databaseMajorVersion = databaseMetaData.getDatabaseMajorVersion();
        assertThat(databaseMajorVersion).isEqualTo(17);

        String databaseProductName = databaseMetaData.getDatabaseProductName();
        assertThat(databaseProductName).containsIgnoringCase("postgresql");
    }

    @Test
    void serviceRead() {
        List<Country> all = countryService.findAll();
        log.info("Nb countries = {}", all.size());
        assertThat(all).hasSizeGreaterThan(100);
    }

    @Test
    void serviceFindCountryFrance() {
        Optional<Country> byId = countryService.findById("FRA");
        assertThat(byId)
                .isNotEmpty();
        assertThat(byId.get())
                .extracting(Country::getName)
                .isEqualTo("France");
    }

    @Test
    @Transactional
    @Rollback
    void serviceWrite() {
        Country newCountry = new Country();
        newCountry.setCode("MRS");
        newCountry.setCode2("M_");
        newCountry.setName("Mars");
        newCountry.setLocalName("👽");
        newCountry.setContinent("Antarctica");
        newCountry.setRegion("Space");
        newCountry.setSurfaceArea(15_200_000.0);
        newCountry.setGovernmentForm("Freedom");
        newCountry.setPopulation(999_999_999L);
        newCountry.setLifeExpectancy(1556.5);
        countryService.save(newCountry);

        Optional<Country> persistedEntity = countryService.findById("MRS");
        assertThat(persistedEntity).isNotEmpty();
    }

}