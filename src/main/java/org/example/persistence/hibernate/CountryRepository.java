package org.example.persistence.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.example.model.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("countryRepositoryHibernate")
public class CountryRepository {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @PersistenceContext
    private EntityManager em;

    private SessionFactory getSessionFactory() {
        return emf.unwrap(SessionFactory.class);
    }

//    private EntityManager createEntityManager() {
//        return emf.createEntityManager();
//    }

    private Session getSession() {
        return em.unwrap(Session.class);
    }

    private StatelessSession getStatelessSession() {
        return getSessionFactory().openStatelessSession();
    }

    public Optional<Country> findById(String code) {
        Session session = getSession();
        return Optional.ofNullable(session.find(Country.class, code));
    }

    public List<Country> findAll() {
        Query<Country> query = getSession()
                .createQuery("from Country ", Country.class);
        return query.getResultList();
    }

    public Country save(Country country) {
        Session session = getSession();
        session.persist(country);
        return country;
    }

    public void delete(String id) {
        Session session = getSession();
        findById(id).ifPresent(session::remove);
    }

}
