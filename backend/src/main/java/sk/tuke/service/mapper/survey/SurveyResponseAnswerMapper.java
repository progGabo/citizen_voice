package sk.tuke.service.mapper.survey;

import org.mapstruct.*;
import sk.tuke.domain.survey.SurveyResponseAnswer;
import sk.tuke.service.dto.survey.SurveyResponseAnswerDTO;
import sk.tuke.service.mapper.EntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {SurveyResponseChoiceMapper.class})
public interface SurveyResponseAnswerMapper extends EntityMapper<SurveyResponseAnswerDTO, SurveyResponseAnswer> {

    @Mapping(source = "questionId", target = "surveyQuestion.id")
    SurveyResponseAnswer toEntity(SurveyResponseAnswerDTO surveyResponseAnswerDTO);

    @Mapping(target = "questionId", source = "surveyQuestion.id")
    SurveyResponseAnswerDTO toDto(SurveyResponseAnswer surveyResponseAnswer);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SurveyResponseAnswer partialUpdate(SurveyResponseAnswerDTO surveyResponseAnswerDTO, @MappingTarget SurveyResponseAnswer surveyResponseAnswer);
}
