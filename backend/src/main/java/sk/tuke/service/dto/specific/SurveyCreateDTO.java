package sk.tuke.service.dto.specific;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sk.tuke.domain.User;
import sk.tuke.domain.survey.SurveyQuestion;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class SurveyCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private Instant createdAt;

    private Instant updatedAt;

    private Long userId;

    private List<SurveyQuestion> surveyQuestions;
}
