package sk.tuke.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sk.tuke.domain.Event;
import sk.tuke.service.dto.EventDTO;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CityMapper.class})
public interface EventMapper extends EntityMapper<EventDTO, Event> {

    @Mapping(source = "cityId", target = "city.id")
    @Mapping(source = "userId", target = "user.id")
    Event toEntity(EventDTO dto);


    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "user.id", target = "userId")
    EventDTO toDto(Event entity);

}
