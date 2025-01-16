package sk.tuke.repository.survey;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.survey.SurveyResponse;

import java.util.Optional;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Integer> {
    Long countBySurveyId(Integer surveyId);

    boolean existsBySurveyIdAndRespondentId(Integer surveyId, Long userId);

    Page<SurveyResponse> findBySurveyId(Pageable pageable, Integer surveyId);
}
