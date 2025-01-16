package sk.tuke.service.dto.specific;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.service.dto.UserInfoDTO;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class PetitionSigneesDTO implements Serializable {

    private UserInfoDTO user;

    private boolean verified;
}
