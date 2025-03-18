package org.example.persistence.data.jpa;

import org.example.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryDataJpaRepository extends JpaRepository<Country, String> {

    List<Country> findAllByName(String name);

    @Query("from Country where capital.name = :name")
    List<Country> findAllByCapitalName(String name);

    List<Country> findAllByNameContaining(String name);

}
