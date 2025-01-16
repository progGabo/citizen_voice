package sk.tuke.service.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.domain.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetitionSigneesDAO {

    private User user;

    private boolean verified;
}
