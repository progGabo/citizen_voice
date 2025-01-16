package sk.tuke.service.impl;

import org.springframework.stereotype.Service;
import sk.tuke.domain.enumeration.QuestionType;
import sk.tuke.domain.survey.SurveyQuestion;
import sk.tuke.repository.survey.*;
import sk.tuke.service.dto.survey.statistics.SurveyStatisticsDTO;
import sk.tuke.service.errors.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SurveyStatisticsServiceImpl {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyResponseAnswerRepository surveyResponseAnswerRepository;
    private final SurveyResponseChoiceRepository surveyResponseChoiceRepository;
    private final SurveyQuestionChoiceRepository surveyQuestionChoiceRepository;
    private final SurveyRepository surveyRepository;

    public SurveyStatisticsServiceImpl(SurveyResponseRepository surveyResponseRepository,
                                       SurveyQuestionRepository surveyQuestionRepository,
                                       SurveyResponseAnswerRepository surveyResponseAnswerRepository,
                                       SurveyResponseChoiceRepository surveyResponseChoiceRepository,
                                       SurveyQuestionChoiceRepository surveyQuestionChoiceRepository,
                                       SurveyRepository surveyRepository) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyResponseAnswerRepository = surveyResponseAnswerRepository;
        this.surveyResponseChoiceRepository = surveyResponseChoiceRepository;
        this.surveyQuestionChoiceRepository = surveyQuestionChoiceRepository;
        this.surveyRepository = surveyRepository;
    }

    public SurveyStatisticsDTO getStatisticsForSurvey(Integer surveyId) throws ResourceNotFoundException {
        if (!surveyRepository.existsById(surveyId))
            throw new ResourceNotFoundException("Survey with id " + surveyId + " does not exists");

        SurveyStatisticsDTO stats = new SurveyStatisticsDTO();
        stats.setSurveyId(surveyId);

        Long totalResponses = surveyResponseRepository.countBySurveyId(surveyId);
        stats.setTotalResponses(totalResponses);

        List<SurveyStatisticsDTO.ScaleQuestionStatsDTO> scaleStats = new ArrayList<>();
        List<SurveyStatisticsDTO.ChoiceQuestionStatsDTO> choiceStats = new ArrayList<>();
        List<SurveyStatisticsDTO.TextQuestionStatsDTO> textStats = new ArrayList<>();

        // vsetky otazky z prieskumu
        List<SurveyQuestion> questions = surveyQuestionRepository.findBySurveyId(surveyId);

        for (SurveyQuestion q : questions) {
            if (q.getQuestionType() == QuestionType.SCALE) {
                int totalVotes = 0;

                List<Object[]> results = surveyResponseAnswerRepository.countScaleAnswersGrouped(q.getId());
                // results: [ [scale_value, count], [scale_value, count], ...]

                // <scaleValue, voteCount>
                Map<Integer, Long> scaleMap = new HashMap<>();

                // priradenie jednotlivych scale hodnot ku voteCountu
                for (Object[] row : results) {
                    Integer scaleValue = (Integer) row[0];
                    Long count = (Long) row[1];
                    scaleMap.put(scaleValue, count);

                    totalVotes += count.intValue();
                }
                // dopln vsetky hodnoti skaly 1 - 5, aj ked maju voteCount == 0
                fillBlanksScaleQType(scaleMap);

                // naplnanie resultu
                SurveyStatisticsDTO.ScaleQuestionStatsDTO s = new SurveyStatisticsDTO.ScaleQuestionStatsDTO();
                s.setQuestionId(q.getId());
                s.setCountsPerValue(scaleMap);
                s.setTotalVotes(totalVotes);
                scaleStats.add(s);

            } else if (q.getQuestionType() == QuestionType.MULTIPLE_CHOICE
                    || q.getQuestionType() == QuestionType.SINGLE_CHOICE) {
                int totalVotes = surveyResponseAnswerRepository.countByTypeAndSurveyQuestionId(q.getQuestionType(), q.getId());

                // spocitanie odpovedi pre kazdu volbu - nevracia choices s voteCount == 0
                List<Object[]> results = surveyResponseChoiceRepository.countChoicesGroupedByChoice(q.getId());

                Map<Integer, Long> choiceMap = new HashMap<>();

                // priradenie jednotlivych IDs ku voteCountu
                for (Object[] row : results) {
                    Integer choiceId = (Integer) row[0];
                    Long count = (Long) row[1];
                    choiceMap.put(choiceId, count);
                }
                // dopln IDcka vsetkych choices, aj ked maju voteCount == 0
                fillBlanksChoiceQType(choiceMap, q.getId());

                // naplnanie resultu
                SurveyStatisticsDTO.ChoiceQuestionStatsDTO c = new SurveyStatisticsDTO.ChoiceQuestionStatsDTO();
                c.setTotalVotes(totalVotes);
                c.setQuestionId(q.getId());
                c.setCountsPerChoice(choiceMap);

                choiceStats.add(c);
            } else if (q.getQuestionType() == QuestionType.TEXT) {
                int totalAnswers = surveyResponseAnswerRepository.countByTypeAndSurveyQuestionId(q.getQuestionType(), q.getId());

                // naplnanie resultu
                SurveyStatisticsDTO.TextQuestionStatsDTO c = new SurveyStatisticsDTO.TextQuestionStatsDTO();
                c.setTotalAnswers(totalAnswers);
                c.setQuestionId(q.getId());
                textStats.add(c);
            }
        }

        stats.setScaleQuestions(scaleStats);
        stats.setChoiceQuestions(choiceStats);
        stats.setTextQuestions(textStats);
        return stats;
    }

    private void fillBlanksScaleQType(Map<Integer, Long> map) {
        for (int i = 1; i <= 5; i++) {
            map.putIfAbsent(i, 0L);
        }
    }

    private void fillBlanksChoiceQType(Map<Integer, Long> map, Integer questionId) {
        List<Integer> allChoicesIds = surveyQuestionChoiceRepository.findAllIdsBySurveyQuestionId(questionId);
        allChoicesIds.forEach(id -> map.putIfAbsent(id, 0L));
    }
}
