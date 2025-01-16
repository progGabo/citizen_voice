package sk.tuke.service.dto.survey;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sk.tuke.service.dto.UserInfoDTO;

import java.io.Serializable;
import java.util.List;

@Data
public class SurveyResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer surveyId;

    private UserInfoDTO respondent;

    @NotNull
    private List<SurveyResponseAnswerDTO> answers;
}
