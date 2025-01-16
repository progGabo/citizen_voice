package sk.tuke.service.dto.survey;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sk.tuke.domain.enumeration.QuestionType;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class SurveyResponseAnswerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private QuestionType type;

    @NotNull
    private Integer questionId;

    // ENUM TEXT
    private String answerContent;

    // ENUM SCALE
    private Integer scaleValue;

    // CHOICE
    private List<SurveyResponseChoiceDTO> surveyResponseChoices;

    private Instant createdAt;

    private Instant updatedAt;

}
