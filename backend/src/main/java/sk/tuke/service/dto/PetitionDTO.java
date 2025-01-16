package sk.tuke.service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetitionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private Integer noSignees;

    private Integer requiredSignees;

    private JsonNode content;

    private Status status = Status.ACTIVE;

    private Boolean authorityNotified;

    private Instant createdAt;

    private Instant updatedAt;

    private String firstName;

    private String lastName;

    private Long cityId;

    private Long userId;
}
