package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.Voting;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.service.dto.specific.VotingSpecificDTO;

import java.util.Optional;

@Repository
public interface VotingRepository extends JpaRepository<Voting, Long> {

    Optional<Voting> findByStatusAndId(Status status, Long id);
    Page<Voting> findAllByStatus(Status status, Pageable pageable);
    Page<Voting> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
    SELECT CASE WHEN COUNT(uv) > 0 THEN true ELSE false END
    FROM UserVoting uv
    JOIN VotingAnswers a ON uv.votingAnswerId = a.id
    JOIN VotingQuestion q ON a.voteQuestion.id = q.id
    JOIN Voting v ON q.voting.id = v.id
    WHERE uv.userId = :userId
      AND v.id = :votingId
""")
    boolean hasUserAnsweredVoting(@Param("userId") Long userId, @Param("votingId") Long votingId);
}
