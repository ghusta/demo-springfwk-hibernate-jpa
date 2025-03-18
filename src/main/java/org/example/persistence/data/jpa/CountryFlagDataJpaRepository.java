package org.example.persistence.data.jpa;

import org.example.model.CountryFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryFlagDataJpaRepository extends JpaRepository<CountryFlag, String> {

}
