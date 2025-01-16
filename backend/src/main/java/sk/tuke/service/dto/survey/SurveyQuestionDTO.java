package sk.tuke.service.dto.survey;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sk.tuke.domain.enumeration.QuestionType;
import sk.tuke.domain.survey.SurveyQuestionChoice;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class SurveyQuestionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull
    @NotEmpty
    private String questionText;

    @NotNull
    private QuestionType questionType;

    @NotNull
    private Boolean isOptional;

    // questions of type CHOICE
    private List<SurveyQuestionChoiceDTO> choices;
}
