package sk.tuke.domain.survey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SurveyResponseChoiceId implements Serializable {
    private static final long serialVersionUID = 5760432851019414556L;
    @NotNull
    @Column(name = "survey_response_answer_id", nullable = false)
    private Integer surveyResponseAnswerId;

    @NotNull
    @Column(name = "choice_id", nullable = false)
    private Integer choiceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SurveyResponseChoiceId entity = (SurveyResponseChoiceId) o;
        return Objects.equals(this.choiceId, entity.choiceId) &&
            Objects.equals(this.surveyResponseAnswerId, entity.surveyResponseAnswerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(choiceId, surveyResponseAnswerId);
    }

}
