package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.Petition;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.service.dto.specific.PetitionSpecificDTO;

@Repository
public interface PetitionRepository extends JpaRepository<Petition, Integer> {

    Page<Petition> findAllByStatus(Pageable pageable, Status status);

    @Query("SELECT p FROM Petition p JOIN p.signees s WHERE s.user.id = :userId")
    Page<Petition> findAllBySigneesUserId(Pageable pageable, @Param("userId") Long userId);

    Page<Petition> findAllByUserId(Pageable pageable, Long id);
}
