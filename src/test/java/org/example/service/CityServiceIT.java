package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.config.BackendConfig;
import org.example.model.City;
import org.example.model.Country;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = BackendConfig.class)
@Testcontainers
class CityServiceIT {

    private static final Logger log = LoggerFactory.getLogger(CityServiceIT.class);

    @Autowired
    private EntityManager em;

    @Container
    static PostgreSQLContainer postgresWorldDB = new PostgreSQLContainer(
            DockerImageName.parse("ghusta/postgres-world-db:2.12").asCompatibleSubstituteFor("postgres"))
            .withImagePullPolicy(PullPolicy.alwaysPull()) // https://java.testcontainers.org/features/advanced_options/#image-pull-policy
            .withDatabaseName("world-db")
            .withUsername("world")
            .withPassword("world123");

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CountryService countryService;

    @Autowired
    private CityService cityService;

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("jdbc.url", () -> postgresWorldDB.getJdbcUrl());
        registry.add("jdbc.username", () -> postgresWorldDB.getUsername());
        registry.add("jdbc.password", () -> postgresWorldDB.getPassword());

        registry.add("unused.jdbc.host", () -> postgresWorldDB.getHost());
        registry.add("unused.jdbc.port", () -> postgresWorldDB.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT));
    }

    @Test
    void saveCity() {
        Country france = countryService.findById("FRA").orElseThrow();

        int guessLastId = (int) cityService.count();

        City city1 = new City();
        city1.setId(guessLastId + 1);
        city1.setName("Le Mirail");
        city1.setLocalName("La Cité");
        city1.setDistrict("Occitanie");
        city1.setPopulation(123);
        city1.setCountry(france);

        cityService.save(city1);

        em.detach(city1);
        em.clear();

        Optional<City> findSaved = cityService.findById(guessLastId + 1);
        assertThat(findSaved).isNotEmpty();
    }
}
