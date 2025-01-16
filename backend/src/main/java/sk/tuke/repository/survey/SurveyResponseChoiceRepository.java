package sk.tuke.repository.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.survey.SurveyResponseChoice;

import java.util.List;

@Repository
public interface SurveyResponseChoiceRepository extends JpaRepository<SurveyResponseChoice, Integer> {
    @Query("SELECT src.choice.id, COUNT(src) " +
        "FROM SurveyResponseChoice src " +
        "WHERE src.surveyResponseAnswer.surveyQuestion.id = :id " +
        "GROUP BY src.choice.id")
    List<Object[]> countChoicesGroupedByChoice(Integer id);
}
