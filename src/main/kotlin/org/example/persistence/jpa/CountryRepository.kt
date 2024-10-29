package org.example.persistence.jpa

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.TypedQuery
import org.example.model.Country
import org.springframework.stereotype.Repository

@Repository("countryRepositoryJpa")
class CountryRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun findById(code: String): Country? {
        return em.find(Country::class.java, code)
    }

    fun findAll(): List<Country> {
        val query: TypedQuery<Country> = em
            .createQuery("from Country ", Country::class.java)
        return query.resultList.toList()
    }

    fun save(country: Country): Country {
        em.persist(country)
        return country
    }
}
