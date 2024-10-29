package org.example.dto;

public record CountryDTO(
        String code,
        String code2,
        String name,
        String continent,
        String region,
        Long population,
        Double lifeExpectancy,
        Double surfaceArea,
        String governmentForm,
        String localName
) {
}