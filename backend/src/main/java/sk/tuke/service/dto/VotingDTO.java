package sk.tuke.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class VotingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private String title;

    private Instant createdAt;

    private Instant updatedAt;

    private Status status;

    @NotNull
    @NotEmpty
    private List<VoteQuestionsDTO> voteQuestions;
}
