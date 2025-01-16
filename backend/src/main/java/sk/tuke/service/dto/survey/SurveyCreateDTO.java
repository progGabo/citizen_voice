package sk.tuke.service.dto.survey;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SurveyCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    @NotNull
    private List<SurveyQuestionDTO> surveyQuestions;
}
