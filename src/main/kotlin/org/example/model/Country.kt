package org.example.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "country")
data class Country(
    @Id
    val code: String,

    @Column(nullable = false)
    val code2: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val continent: String,

    @Column(nullable = false)
    val region: String,

    @Column(nullable = false)
    val population: Long,

    @Column(name = "life_expectancy")
    val lifeExpectancy: Double? = null,

    @Column(name = "surface_area", nullable = false)
    val surfaceArea: Double,

    @Column(name = "government_form", nullable = false)
    val governmentForm: String,

    @Column(name = "local_name", nullable = false)
    val localName: String
)


