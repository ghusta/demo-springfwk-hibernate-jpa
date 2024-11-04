package org.example.service;

import org.example.model.City;
import org.example.persistence.hibernate.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public Optional<City> findById(String id) {
        return cityRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<City> findByCountry(String code) {
        return cityRepository.findByCountry(code);
    }

    @Transactional(readOnly = true)
    public Optional<City> findCapital(String countryCode) {
        return cityRepository.findCapital(countryCode);
    }

}
