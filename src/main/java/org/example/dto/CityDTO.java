package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record CityDTO(
        @JsonIgnore Integer id,
        String name,
        String district,
        Integer population,
        String localName
) {
}
