package org.example.controller

import org.example.model.Country
import org.example.service.CountryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/countries")
class CountryController(val countryService: CountryService) {

    @GetMapping("")
    fun findAll(): List<Country> {
        return countryService.findAll()
    }

    @GetMapping("{code}")
    fun findByCode(@PathVariable("code") code: String): Country? {
        return countryService.findById(code) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
