package org.example.config;

import org.example.model.Country;
import org.example.service.CountryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BackendConfigTest {

    private static final Logger log = LoggerFactory.getLogger(BackendConfigTest.class);

    private static ApplicationContext ctx;

    @BeforeAll
    static void setUpGlobal() {
        ctx = new AnnotationConfigApplicationContext(BackendConfig.class);
    }

    @Test
    void countBeans() {
        int beanDefinitionCount = ctx.getBeanDefinitionCount();
        assertThat(beanDefinitionCount).isGreaterThan(10);
    }

    @Test
    void listBeans() {
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        assertThat(beanDefinitionNames)
                .hasSizeGreaterThan(10)
                .contains("entityManagerBean", "entityManagerFactoryBean", "dataSource", "txManager")
                .contains("countryService", "countryRepository");
    }

    @Test
    @Disabled("Needs local database -- see IT")
    void serviceRead() {
        CountryService countryService = ctx.getBean(CountryService.class);
        List<Country> all = countryService.findAll();
        log.info("Nb countries = {}", all.size());
        assertThat(all).hasSizeGreaterThan(100);
    }

    @Test
    @Disabled("Needs local database -- see IT")
    void serviceWrite() {
        CountryService countryService = ctx.getBean(CountryService.class);
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