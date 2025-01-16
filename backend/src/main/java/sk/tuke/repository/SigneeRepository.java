package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.Petition;
import sk.tuke.domain.Signee;
import sk.tuke.domain.User;
import sk.tuke.service.dao.PetitionSigneesDAO;
import sk.tuke.service.dao.SignedPetitionWrapperDAO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SigneeRepository extends JpaRepository<Signee, Integer> {

    boolean existsByUserIdAndPetitionId(Long userId, Integer petitionId);

    Optional<Signee> findByUserIdAndPetitionId(Long userId, Integer petitionId);

    Optional<Signee> findByIdAndUserIdAndPetitionId(Integer signeeId, Long userId, Integer petitionId);

    List<Signee> findAllByUserId(Long userId);

    @Query("SELECT s.petition FROM Signee s WHERE s.user.id = :userId")
    List<Petition> findPetitionsByUserId(Long userId);

    @Query("SELECT new sk.tuke.service.dao.SignedPetitionWrapperDAO(s.petition, s.verified) " +
        "FROM Signee s " +
        "WHERE s.user.id = :userId")
    List<SignedPetitionWrapperDAO> findSignedPetitionsByUserId(Long userId);

    @Query("SELECT new sk.tuke.service.dao.PetitionSigneesDAO(s.user, s.verified) " +
        "FROM Signee s " +
        "WHERE s.petition.id = :id")
    List<PetitionSigneesDAO> findAllSigneesByPetitionId(Integer id);
}
