package sk.tuke.service.dto.specific;

import lombok.Data;
import sk.tuke.domain.enumeration.AuthRole;

@Data
public class UserTokenDetails {

    private String email;

    private AuthRole role;

    private Long userId;

    private Integer petitionId;

    private Integer signeeId;
}
