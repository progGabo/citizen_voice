package sk.tuke.service.mapper.survey;

import org.mapstruct.*;
import sk.tuke.domain.survey.SurveyResponseAnswer;
import sk.tuke.domain.survey.SurveyResponseChoice;
import sk.tuke.service.dto.survey.SurveyResponseAnswerDTO;
import sk.tuke.service.dto.survey.SurveyResponseChoiceDTO;
import sk.tuke.service.mapper.EntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SurveyResponseChoiceMapper extends EntityMapper<SurveyResponseChoiceDTO, SurveyResponseChoice> {

    @InheritInverseConfiguration
    SurveyResponseChoice toEntity(SurveyResponseChoiceDTO surveyResponseChoiceDTO);

//    @Mapping(target = "questionChoiceId", source = "id.choiceId")
    @Mapping(target = "surveyResponseAnswerId", source = "id.surveyResponseAnswerId")
    @Mapping(target = "questionChoiceId", source = "id.choiceId")
    SurveyResponseChoiceDTO toDto(SurveyResponseChoice surveyResponseChoice);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SurveyResponseChoice partialUpdate(SurveyResponseChoiceDTO surveyResponseChoiceDTO, @MappingTarget SurveyResponseChoice surveyResponseChoice);
}
