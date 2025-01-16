package sk.tuke.service.dto.specific;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignedPetitionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PetitionSpecificDTO petition;

    private Boolean verified;
}
