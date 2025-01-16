package sk.tuke.service.dao;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.domain.Petition;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedPetitionWrapperDAO {
    private Petition petition;
    private Boolean verified;
}
