package org.example.service;

import org.example.model.Country;
import org.example.persistence.data.jpa.CountryDataJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CountryDataJpaService {

    private final CountryDataJpaRepository countryDataJpaRepository;

    public CountryDataJpaService(CountryDataJpaRepository countryDataJpaRepository) {
        this.countryDataJpaRepository = countryDataJpaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Country> findById(String code) {
        return countryDataJpaRepository.findById(code);
    }

    @Transactional(readOnly = true)
    public List<Country> findAll() {
        return countryDataJpaRepository.findAll();
    }

    public Country save(Country country) {
        return countryDataJpaRepository.save(country);
    }

}
