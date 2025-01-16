package sk.tuke.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sk.tuke.domain.Signee;
import sk.tuke.service.dto.SigneeDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PetitionMapper.class})
public interface SigneeMapper extends EntityMapper<SigneeDTO, Signee> {
}
