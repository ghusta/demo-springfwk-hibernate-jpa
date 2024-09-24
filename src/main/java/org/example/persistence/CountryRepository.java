package org.example.persistence;

import org.example.model.Country;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class CountryRepository {

    @PersistenceContext
    private EntityManager em;

//    private SessionFactory getSessionFactory() {
//        return emf.unwrap(SessionFactory.class);
//    }

//    private EntityManager createEntityManager() {
//        return emf.createEntityManager();
//    }

    private Session getSession() {
        return em.unwrap(Session.class);
    }

    public Optional<Country> findById(String code) {
        Session session = getSession();
        return Optional.ofNullable(session.get(Country.class, code));
    }

    public List<Country> findAll() {
        Query<Country> query = getSession()
                .createQuery("from Country ", Country.class);
        return query.getResultList();
    }

    public Country save(Country country) {
        Session session = getSession();
        session.save(country);
        return country;
    }

}
