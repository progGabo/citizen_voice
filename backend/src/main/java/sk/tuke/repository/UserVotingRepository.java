package sk.tuke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.UserVoting;

@Repository
public interface UserVotingRepository extends JpaRepository<UserVoting, Long> {

    boolean existsByUserIdAndVotingAnswerId(Long userId, Long votingAnswerId);
}
