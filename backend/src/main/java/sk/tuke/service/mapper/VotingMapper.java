package sk.tuke.service.mapper;

import org.mapstruct.*;
import sk.tuke.domain.Voting;
import sk.tuke.service.dto.VotingDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VotingMapper extends EntityMapper<VotingDTO, Voting> {
    /*@Mapping(source = "userId", target = "user.id")
    Voting toEntity(VotingDTO dto);*/

    VotingDTO toDto(Voting entity);
}
