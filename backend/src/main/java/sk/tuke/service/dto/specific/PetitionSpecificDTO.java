package sk.tuke.service.dto.specific;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import sk.tuke.domain.enumeration.Status;

import java.io.Serializable;
import java.time.Instant;

@Data
public class PetitionSpecificDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private JsonNode content;

    private Integer noSignees;

    private Integer requiredSignees;

    private String firstName;

    private String lastName;

    private Status status;

    private Boolean authorityNotified;

    private String cityId;

    private Instant createdAt;
}
