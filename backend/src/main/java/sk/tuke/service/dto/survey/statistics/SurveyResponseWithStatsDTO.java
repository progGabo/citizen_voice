package sk.tuke.service.dto.survey.statistics;

import lombok.Data;
import sk.tuke.service.dto.survey.SurveyResponseDTO;

@Data
public class SurveyResponseWithStatsDTO {
    private SurveyResponseDTO response;
    private SurveyStatisticsDTO statistics;
}
