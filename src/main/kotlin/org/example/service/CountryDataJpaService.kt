package org.example.service

import org.example.model.Country
import org.example.persistence.data.jpa.CountryDataJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * `open` not needed thanks to kotlin spring plugin.
 */
@Service
@Transactional
class CountryDataJpaService(private val countryDataJpaRepository: CountryDataJpaRepository) {

    @Transactional(readOnly = true)
    fun findById(code: String): Country? {
        return countryDataJpaRepository.findByIdOrNull(code)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Country> {
        return countryDataJpaRepository.findAll().toList()
    }

    fun save(country: Country): Country {
        return countryDataJpaRepository.save(country)
    }
}
