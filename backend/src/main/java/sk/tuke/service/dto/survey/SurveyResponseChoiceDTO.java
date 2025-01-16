package sk.tuke.service.dto.survey;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyResponseChoiceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer surveyResponseAnswerId;
    private Integer questionChoiceId;
}
