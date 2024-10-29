package org.example.persistence.hibernate

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.example.model.Country
import org.hibernate.Session
import org.hibernate.query.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository("countryRepositoryHibernate")
class CountryRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    //    private SessionFactory getSessionFactory() {
    //        return emf.unwrap(SessionFactory.class);
    //    }
    //    private EntityManager createEntityManager() {
    //        return emf.createEntityManager();
    //    }
    private fun getSession(): Session {
        return em.unwrap(Session::class.java)
    }

    fun findById(code: String): Country? {
        val session: Session = getSession()
        return session.get(Country::class.java, code)
    }

    fun findAll(): List<Country> {
        val query: Query<Country> = getSession()
            .createQuery("from Country ", Country::class.java)
        return query.resultList.toList()
    }

    fun save(country: Country): Country {
        val session: Session = getSession()
        session.persist(country)
        return country
    }

    fun delete(id: String) {
        val session: Session = getSession()
        findById(id)?.let { session::remove }
    }
}
