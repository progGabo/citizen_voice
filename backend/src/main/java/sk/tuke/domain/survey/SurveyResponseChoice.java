package sk.tuke.domain.survey;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "survey_response_choice", schema = "cv1")
public class SurveyResponseChoice {
    @EmbeddedId
    private SurveyResponseChoiceId id;

    @MapsId("surveyResponseAnswerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_response_answer_id", nullable = false)
    private SurveyResponseAnswer surveyResponseAnswer;

    @MapsId("choiceId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choice_id", nullable = false)
    private SurveyQuestionChoice choice;

}
