package org.example.service

import org.example.model.Country
import org.example.persistence.hibernate.CountryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * `open` not needed thanks to kotlin spring plugin.
 */
@Service
@Transactional
class CountryService(private val countryRepository: CountryRepository) {

    @Transactional(readOnly = true)
    fun findById(code: String): Country? {
        return countryRepository.findById(code)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Country> {
        return countryRepository.findAll().toList()
    }

    fun save(country: Country): Country {
        return countryRepository.save(country)
    }

    fun deleteById(id: String) {
        countryRepository.delete(id)
    }
}
