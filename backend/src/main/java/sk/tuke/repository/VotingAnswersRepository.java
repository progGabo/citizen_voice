package sk.tuke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.tuke.domain.VotingAnswers;

public interface VotingAnswersRepository extends JpaRepository<VotingAnswers, Long> {
}
