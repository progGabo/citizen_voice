package sk.tuke.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;

@Data
public class VoteAnswersDTO implements Serializable {

    private Long id;

    @NotNull
    private String content;

    private Integer voteCount;
}
