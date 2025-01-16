package sk.tuke.service.dto.specific;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.service.dto.VoteQuestionsDTO;

import java.io.Serializable;
import java.util.List;

@Data
public class VotingSpecificDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String firstname;

    private String lastname;

    private Integer voteCount;

    private String title;

    private Status status;

    @NotNull
    @NotEmpty
    private List<VoteQuestionsDTO> voteQuestions;

}


