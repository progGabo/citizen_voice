package sk.tuke.service.mapper;

import org.mapstruct.*;
import sk.tuke.domain.User;
import sk.tuke.service.dto.UserInfoDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = AddressMapper.class)
public interface UserInfoMapper extends EntityMapper<UserInfoDTO, User> {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserInfoDTO userInfoDTO, @MappingTarget User user);
}
