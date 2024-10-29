package org.example.persistence.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.model.City;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("cityRepositoryHibernate")
public class CityRepository {

    @PersistenceContext
    private EntityManager em;

    private Session getSession() {
        return em.unwrap(Session.class);
    }

    public Optional<City> findById(String id) {
        Session session = getSession();
        return Optional.ofNullable(session.get(City.class, id));
    }

    public Optional<City> findCapital(String countryCode) {
        Query<City> query = getSession()
                .createQuery("select c.capital from Country c where c.code = :countryCode", City.class);
        query.setParameter("countryCode", countryCode);
        return query.uniqueResultOptional();
    }

}
