package sk.tuke.service.dto.specific;

import lombok.Data;

import java.io.Serializable;

@Data
public class VoteStatisticDTO implements Serializable {

    private Long AnswerId;

    private Integer voteCount;

    private Float percentage;
}
