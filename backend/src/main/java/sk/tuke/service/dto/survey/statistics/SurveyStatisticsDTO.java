package sk.tuke.service.dto.survey.statistics;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SurveyStatisticsDTO {

    private Integer surveyId;

    private Long totalResponses;

    private List<ScaleQuestionStatsDTO> scaleQuestions;

    private List<ChoiceQuestionStatsDTO> choiceQuestions;

    private List<TextQuestionStatsDTO> textQuestions;
    @Data
    public static class ScaleQuestionStatsDTO {

        private Integer questionId;

        private Integer totalVotes;

        // Mapuje scale_value -> count
        private Map<Integer, Long> countsPerValue;
    }

    @Data
    public static class ChoiceQuestionStatsDTO {

        private Integer questionId;

        private Integer totalVotes;

        // Mapa choice_id -> count
        private Map<Integer, Long> countsPerChoice;
    }

    @Data
    public static class TextQuestionStatsDTO {

        private Integer questionId;

        private Integer totalAnswers;
    }
}
