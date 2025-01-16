package sk.tuke.repository.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.enumeration.QuestionType;
import sk.tuke.domain.survey.SurveyResponseAnswer;

import java.util.List;

@Repository
public interface SurveyResponseAnswerRepository extends JpaRepository<SurveyResponseAnswer, Integer> {
    @Query("SELECT sra.scaleValue, COUNT(sra) " +
        "FROM SurveyResponseAnswer sra " +
        "WHERE sra.surveyQuestion.id = :id " +
        "GROUP BY sra.scaleValue")
    List<Object[]> countScaleAnswersGrouped(Integer id);

    Integer countByTypeAndSurveyQuestionId(QuestionType type, Integer questionId);
}
