package sk.tuke.service.mapper.specific;

import org.mapstruct.*;
import sk.tuke.domain.Event;
import sk.tuke.service.dto.EventDTO;
import sk.tuke.service.dto.specific.EventCreateDTO;
import sk.tuke.service.mapper.EntityMapper;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventCreateMapper extends EntityMapper<EventCreateDTO, Event> {

    @Mapping(source = "city.id", target = "cityId")
    EventCreateDTO toDto(Event entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event partialUpdate(EventCreateDTO eventCreateDTO, @MappingTarget Event event);
}
