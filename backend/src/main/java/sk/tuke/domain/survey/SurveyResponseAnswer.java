package sk.tuke.domain.survey;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sk.tuke.domain.enumeration.QuestionType;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "survey_response_answer", schema = "cv1")
public class SurveyResponseAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType type;

    @Column(name = "answer_content")
    private String answerContent;

    @Column(name = "scale_value")
    private Integer scaleValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "surveyResponseAnswer", cascade = CascadeType.ALL)
    private List<SurveyResponseChoice> surveyResponseChoices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_response_id")
    private SurveyResponse surveyResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question_id")
    private SurveyQuestion surveyQuestion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SurveyResponseAnswer that = (SurveyResponseAnswer) o;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "SurveyResponseAnswer{" +
            "id=" + id +
            ", answerContent='" + answerContent + '\'' +
            ", scaleValue=" + scaleValue +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
