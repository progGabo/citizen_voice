package sk.tuke.repository.survey;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.survey.Survey;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    List<Survey> findAllByAuthorId(Integer authorId);
}
