package sk.tuke.service.dto.specific;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.domain.enumeration.AuthRole;
import sk.tuke.domain.enumeration.Gender;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Email
    @NotNull
    @Size(min = 5, max = 254)
    private String email;

    private Boolean alertsOn = false;

    @NotNull
    private AuthRole role;

    private Gender gender;

    private ActiveStatus activeStatus = ActiveStatus.NONE;

    private LocalDate dateOfBirth;

    @NotNull
    @Size(min = 4, max = 120)
    private String passwd;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    private String title;

    private Instant createdAt;

    private Instant updatedAt;

}
