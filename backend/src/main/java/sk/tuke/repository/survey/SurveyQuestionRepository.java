package sk.tuke.repository.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.survey.SurveyQuestion;

import java.util.List;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Integer> {
    List<SurveyQuestion> findBySurveyId(Integer surveyId);
}
