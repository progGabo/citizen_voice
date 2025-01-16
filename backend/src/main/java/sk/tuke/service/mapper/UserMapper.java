package sk.tuke.service.mapper;

import org.mapstruct.*;
import sk.tuke.domain.User;
import sk.tuke.service.dto.UserDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends EntityMapper<UserDTO, User> {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDTO userDTO, @MappingTarget User user);
}
