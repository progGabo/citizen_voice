package sk.tuke.service.dto.create;

import lombok.Data;
import sk.tuke.domain.VotingQuestion;
import sk.tuke.service.dto.VoteQuestionsDTO;

import java.io.Serializable;
import java.util.List;

@Data
public class VotingCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;

    private String title;

    private List<VoteQuestionsDTO> voteQuestions;

}
