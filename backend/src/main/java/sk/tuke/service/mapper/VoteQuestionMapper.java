package sk.tuke.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sk.tuke.domain.VotingQuestion;
import sk.tuke.service.dto.VoteQuestionsDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VoteQuestionMapper extends EntityMapper<VoteQuestionsDTO, VotingQuestion> {
}
