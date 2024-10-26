package org.example.persistence.data.jpa

import org.example.model.Country
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CountryDataJpaRepository : JpaRepository<Country, String>
