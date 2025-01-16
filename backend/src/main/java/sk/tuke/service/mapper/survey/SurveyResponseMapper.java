package sk.tuke.service.mapper.survey;

import org.mapstruct.*;
import sk.tuke.domain.survey.SurveyResponse;
import sk.tuke.domain.survey.SurveyResponseChoice;
import sk.tuke.service.dto.survey.SurveyResponseChoiceDTO;
import sk.tuke.service.dto.survey.SurveyResponseDTO;
import sk.tuke.service.mapper.EntityMapper;
import sk.tuke.service.mapper.UserInfoMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {SurveyResponseAnswerMapper.class, UserInfoMapper.class})
public interface SurveyResponseMapper extends EntityMapper<SurveyResponseDTO, SurveyResponse> {

    @Mapping(source = "surveyId", target = "survey.id")
    @Mapping(source = "answers", target = "surveyResponseAnswers")
    SurveyResponse toEntity(SurveyResponseDTO surveyResponseDTO);

    @Mapping(target = "surveyId", source = "survey.id")
    @Mapping(target = "answers", source = "surveyResponseAnswers")
    SurveyResponseDTO toDto(SurveyResponse surveyResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SurveyResponse partialUpdate(SurveyResponseDTO surveyResponseDTO, @MappingTarget SurveyResponse surveyResponse);
}
