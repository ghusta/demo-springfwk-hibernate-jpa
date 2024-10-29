package org.example.controller;

import org.example.dto.CountryDTO;
import org.example.mapper.CountryMapper;
import org.example.model.City;
import org.example.model.Country;
import org.example.service.CityService;
import org.example.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "/countries")
public class CountryController {

    private final CountryService countryService;
    private final CityService cityService;
    private final CountryMapper countryMapper;

    public CountryController(CountryService countryService, CityService cityService, CountryMapper countryMapper) {
        this.countryService = countryService;
        this.cityService = cityService;
        this.countryMapper = countryMapper;
    }

    @GetMapping(path = "")
    public List<Country> findAll() {
        return countryService.findAll();
    }

    @GetMapping(path = "{code}")
    public CountryDTO findByCode(@PathVariable("code") String code) {
        return countryService.findById(code)
                .map(countryMapper::countryToCountryDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "{code}/capital")
    public String findCapital(@PathVariable("code") String code) {
        return cityService.findCapital(code)
                .map(City::getName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
