package sk.tuke.service.mapper.specific;

import org.mapstruct.*;
import sk.tuke.domain.Article;
import sk.tuke.domain.Voting;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.dto.specific.VotingSpecificDTO;
import sk.tuke.service.mapper.EntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VotingSpecificMapper extends EntityMapper<VotingSpecificDTO, Voting> {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Voting partialUpdate(VotingSpecificDTO votingSpecificDTO, @MappingTarget Voting voting);

    @Mapping(source = "user.firstName", target = "firstname")
    @Mapping(source = "user.lastName", target = "lastname")
    VotingSpecificDTO toDto(Voting entity);
}
