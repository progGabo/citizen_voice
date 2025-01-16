package sk.tuke.service.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyWrapperDTO implements Serializable {

    private Long totalRespondents;

    private SurveyDTO survey;
}
