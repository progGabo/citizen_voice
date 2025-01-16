package sk.tuke.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sk.tuke.domain.Address;
import sk.tuke.domain.Event;
import sk.tuke.service.dto.AddressDTO;
import sk.tuke.service.dto.EventDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper extends EntityMapper<AddressDTO, Address>{

    Address toEntity(AddressDTO dto);

    AddressDTO toDto(Address entity);
}
