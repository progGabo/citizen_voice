package sk.tuke.service.dto.specific;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;

@Data
public class PetitionCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String name;

    private Integer requiredSignees;

    @NotNull
    private JsonNode content;

    private Long cityId;
}
