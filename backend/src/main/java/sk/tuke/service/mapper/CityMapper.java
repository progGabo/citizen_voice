package sk.tuke.service.mapper;

import org.mapstruct.*;
import sk.tuke.domain.City;
import sk.tuke.service.dto.CityDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityMapper extends EntityMapper<CityDTO, City> {
}
