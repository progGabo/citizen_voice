package sk.tuke.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.tuke.domain.User;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.dto.specific.UserRegisterDTO;

import java.util.Optional;

public interface UserService {

    UserRegisterDTO registerUser(UserRegisterDTO userDTO);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByIdSpecific(Long id);

    Optional<UserInfoDTO> findUserByLogin(String email);

    Optional<UserInfoDTO> findUserById(Long id);

    Page<UserInfoDTO> getAllManagedUsers(Pageable pageable);

    void changePassword(String currentClearTextPassword, String newPassword);

    void deleteUser(Long id);

    Page<UserInfoDTO> getPendingOrganizations(Pageable pageable);

    UserInfoDTO activateOrganization(User org);

    UserInfoDTO updateSettings(User user);
    }
