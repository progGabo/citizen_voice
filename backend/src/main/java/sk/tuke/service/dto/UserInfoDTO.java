package sk.tuke.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.domain.enumeration.AuthRole;
import sk.tuke.domain.enumeration.Gender;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Size(max = 60)
    private String firstName;

    @Size(max = 60)
    private String lastName;

    private String title;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private Gender gender;

    private AuthRole role;

    private Boolean alertsOn = false;

    private ActiveStatus activeStatus;

    private LocalDate dateOfBirth;

    private AddressDTO address;

    private Instant createdAt;

    private Instant updatedAt;

}
