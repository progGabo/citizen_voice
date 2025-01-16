package sk.tuke.service.dto.survey;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

import static sk.tuke.domain.Constants.SURVEY_QUESTION_MAX_LENGTH;

@Data
public class SurveyQuestionChoiceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull
    @NotEmpty
    private String choiceText;

    private Instant createdAt;

    private Instant updatedAt;
}
