package sk.tuke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.tuke.domain.VotingQuestion;

public interface VotingQuestionRepository extends JpaRepository<VotingQuestion, Long> {
}
