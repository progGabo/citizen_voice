package sk.tuke.service.mapper.survey;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sk.tuke.domain.survey.Survey;
import sk.tuke.service.dto.survey.SurveyDTO;
import sk.tuke.service.mapper.EntityMapper;
import sk.tuke.service.mapper.UserInfoMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {SurveyQuestionMapper.class, UserInfoMapper.class})
public interface SurveyMapper extends EntityMapper<SurveyDTO, Survey> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Survey toEntity(SurveyDTO dto);
}
