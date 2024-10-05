package org.example.persistence.jpa;

import org.example.model.Country;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository("countryRepositoryJpa")
public class CountryRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Country> findById(String code) {
        return Optional.ofNullable(em.find(Country.class, code));
    }

    public List<Country> findAll() {
        TypedQuery<Country> query = em
                .createQuery("from Country ", Country.class);
        return query.getResultList();
    }

    public Country save(Country country) {
        em.persist(country);
        return country;
    }

}
