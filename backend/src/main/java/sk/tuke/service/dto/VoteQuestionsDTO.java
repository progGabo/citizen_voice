package sk.tuke.service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class VoteQuestionsDTO implements Serializable {

    private Long id;

    @NotNull
    private String content;

    private Integer voteCount;

    @NotNull
    private Boolean mandatory;

    @NotNull
    @NotEmpty
    private List<VoteAnswersDTO> voteAnswers;

}
