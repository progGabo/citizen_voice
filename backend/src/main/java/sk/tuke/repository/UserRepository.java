package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.domain.enumeration.AuthRole;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "role")
    Optional<User> findOneWithRoleByEmail(String login);

    @EntityGraph(attributePaths = "role")
    Optional<User> findOneWithRoleByEmailIgnoreCase(String email);

    Optional<User> findByIdAndActiveStatus(Long id, ActiveStatus activeStatus);

    Page<User> findAllByActiveStatus(Pageable pageable, ActiveStatus activeStatus);

}
