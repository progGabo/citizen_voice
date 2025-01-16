package sk.tuke.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * A DTO representing a user, with only the public attributes.
 */
@Getter
@Setter
public class UserDTO implements Serializable {

    // TODO tato trieda uz barz nema vyznam - nahradit inou, napr UserInfoDTO
    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;



    // prettier-ignore
    @Override
    public String toString() {
        return "UserDTO{" +
            "id='" + id + '\'' +
            ", login='" + login + '\'' +
            "}";
    }
}
