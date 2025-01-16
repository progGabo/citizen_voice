package sk.tuke.service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Data
public class SigneeDTO implements Serializable {

    private Integer id;

    private Instant dateOfSigning;

    private Boolean verified;

    private Instant createdAt;

    private Instant updatedAt;

    private CityDTO city;

    private UserDTO user;

    private PetitionDTO petition;
}
