package org.example.mapper;

import org.example.dto.CityDTO;
import org.example.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityMapper {

    CityDTO cityToCityDTO(City city);

}
