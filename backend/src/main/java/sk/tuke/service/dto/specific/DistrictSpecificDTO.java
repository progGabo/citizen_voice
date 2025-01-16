package sk.tuke.service.dto.specific;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class DistrictSpecificDTO implements Serializable {

    private Integer id;

    private String name;

    private Instant createdAt;

    private Instant updatedAt;

}
