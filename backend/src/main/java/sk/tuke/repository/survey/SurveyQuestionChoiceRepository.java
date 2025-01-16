package sk.tuke.repository.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.survey.SurveyQuestionChoice;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyQuestionChoiceRepository extends JpaRepository<SurveyQuestionChoice, Integer> {

    @Query("select qch.id from SurveyQuestionChoice qch where qch.surveyQuestion.id = :questionId")
    List<Integer> findAllIdsBySurveyQuestionId(Integer questionId);

    @Query("SELECT src.choice.id, COUNT(src) " +
        "FROM SurveyResponseChoice src " +
        "WHERE src.surveyResponseAnswer.surveyQuestion.id = :questionId " +
        "GROUP BY src.choice.id")
    List<Object[]> countChoicesGroupedByChoice(@Param("questionId") Integer questionId);
}
