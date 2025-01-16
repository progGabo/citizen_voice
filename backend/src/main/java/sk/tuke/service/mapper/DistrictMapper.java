package sk.tuke.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sk.tuke.domain.District;
import sk.tuke.service.dto.DistrictDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DistrictMapper extends EntityMapper<DistrictDTO, District> {
}
