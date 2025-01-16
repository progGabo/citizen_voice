package sk.tuke.service.mapper.specific;

import org.mapstruct.*;
import sk.tuke.domain.City;
import sk.tuke.service.dto.specific.CitySpecificDTO;
import sk.tuke.service.mapper.EntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CitySpecificMapper extends EntityMapper<CitySpecificDTO, City> {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    City partialUpdate(CitySpecificDTO cityDTO, @MappingTarget City city);
}
