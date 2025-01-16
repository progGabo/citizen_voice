package sk.tuke.service.mapper.survey;

import org.mapstruct.*;
import sk.tuke.domain.survey.SurveyQuestion;
import sk.tuke.service.dto.survey.SurveyQuestionDTO;
import sk.tuke.service.mapper.EntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SurveyQuestionMapper extends EntityMapper<SurveyQuestionDTO, SurveyQuestion> {


    SurveyQuestion toEntity(SurveyQuestionDTO dto);


}
