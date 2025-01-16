package sk.tuke.service.dto.survey;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.service.dto.UserInfoDTO;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private Status status;

    private UserInfoDTO author;

    @NotNull
    private List<SurveyQuestionDTO> surveyQuestions;
}
