package sk.tuke.service.mapper;

import org.mapstruct.*;
import sk.tuke.domain.User;
import sk.tuke.service.dto.specific.UserRegisterDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserRegisterMapper extends EntityMapper<UserRegisterDTO, User> {

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "city", ignore = true)
    User toEntity(UserRegisterDTO userRegisterDTO);

    UserRegisterDTO toDto(User user);
}
