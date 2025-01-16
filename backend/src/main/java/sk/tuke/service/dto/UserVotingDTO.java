package sk.tuke.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserVotingDTO implements Serializable {
    @NotNull
    @NotEmpty
    private List<Long> AnswerId;
}
