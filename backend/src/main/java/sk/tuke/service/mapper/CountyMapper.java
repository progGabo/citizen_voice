package sk.tuke.service.mapper;

import org.mapstruct.*;
import sk.tuke.domain.County;
import sk.tuke.service.dto.CountyDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CountyMapper extends EntityMapper<CountyDTO, County>{

    County toEntity(CountyDTO countyDTO);

    CountyDTO toDto(County county);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    County partialUpdate(CountyDTO countyDTO, @MappingTarget County county);
}
