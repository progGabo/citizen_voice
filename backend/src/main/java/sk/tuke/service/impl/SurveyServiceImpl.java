package sk.tuke.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.domain.enumeration.QuestionType;
import sk.tuke.domain.survey.*;
import sk.tuke.repository.survey.SurveyQuestionChoiceRepository;
import sk.tuke.repository.survey.SurveyQuestionRepository;
import sk.tuke.repository.survey.SurveyRepository;
import sk.tuke.repository.survey.SurveyResponseRepository;
import sk.tuke.service.SurveyService;
import sk.tuke.service.dto.survey.*;
import sk.tuke.service.errors.ResourceNotFoundException;
import sk.tuke.service.errors.ValidationFailureException;
import sk.tuke.service.mapper.survey.SurveyMapper;
import sk.tuke.service.mapper.survey.SurveyResponseAnswerMapper;
import sk.tuke.service.mapper.survey.SurveyResponseMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SurveyServiceImpl implements SurveyService {
    private static final Logger log = LoggerFactory.getLogger(SurveyServiceImpl.class);

    private final SurveyRepository surveyRepository;

    private final SurveyQuestionRepository surveyQuestionRepository;

    private final SurveyResponseMapper surveyResponseMapper;

    private final SurveyResponseRepository surveyResponseRepository;

    private final SurveyMapper surveyMapper;
    private final SurveyResponseAnswerMapper surveyResponseAnswerMapper;

    private final SurveyQuestionChoiceRepository surveyQuestionChoiceRepository;

    public SurveyServiceImpl(SurveyRepository surveyRepository, SurveyMapper surveyMapper,
                             SurveyQuestionRepository surveyQuestionRepository,
                             SurveyResponseRepository surveyResponseRepository,
                             SurveyResponseMapper surveyResponseMapper,
                             SurveyResponseAnswerMapper surveyResponseAnswerMapper,
                             SurveyQuestionChoiceRepository surveyQuestionChoiceRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyResponseMapper = surveyResponseMapper;
        this.surveyResponseAnswerMapper = surveyResponseAnswerMapper;
        this.surveyQuestionChoiceRepository = surveyQuestionChoiceRepository;
    }

    @Override
    public List<SurveyWrapperDTO> findAllbyUser(Integer authorId) {
        List<Survey> surveys = surveyRepository.findAllByAuthorId(authorId);
        List<SurveyWrapperDTO> res = new ArrayList<>();

        surveys.forEach(s -> {
            SurveyWrapperDTO sw = new SurveyWrapperDTO();
            sw.setSurvey(surveyMapper.toDto(s));
            sw.setTotalRespondents(surveyResponseRepository.countBySurveyId(s.getId()));
            res.add(sw);
        });

        return res;
    }

    @Override
    public SurveyResponseDTO processResponse(SurveyResponseDTO response) throws ResourceNotFoundException, ValidationFailureException{
        log.debug("Request to process survey response with survey id: {}", response.getSurveyId());
        Survey survey = surveyRepository.findById(response.getSurveyId())
            .orElseThrow(() -> new ResourceNotFoundException("Could not find survey with given id: " + response.getSurveyId().toString()));

        // validate answers
        validateSurveyQuestionOptionality(response.getAnswers(), survey.getSurveyQuestions());
        response.getAnswers().forEach(this::validateResponseAnswer);

        // find questionType based on attributes
        findAndSetQuestionTypes(response, survey.getSurveyQuestions());

        SurveyResponse result = surveyResponseMapper.toEntity(response);
        result.setSurvey(survey);

        List<SurveyQuestionChoice> surveyQuestionChoices = new ArrayList<>();
        response.getAnswers().forEach(answ -> {
            // check if answer is of type SINGLE/MULTI CHOICE
            if (answ.getType() == QuestionType.SCALE || answ.getType() == QuestionType.TEXT) {
                return;
            }

            answ.getSurveyResponseChoices().forEach(sci -> {
                surveyQuestionChoiceRepository.findById(sci.getQuestionChoiceId()).ifPresent(surveyQuestionChoices::add);
            });
        });

        result.getSurveyResponseAnswers().forEach( sra -> {
            sra.setSurveyResponse(result);

            setSurveyResponseChoices(surveyQuestionChoices, sra);
        });

        surveyResponseRepository.save(result);
        return surveyResponseMapper.toDto(result);
    }

    private void findAndSetQuestionTypes(SurveyResponseDTO response, List<SurveyQuestion> questions) {
        if (response == null || response.getAnswers() == null || response.getAnswers().isEmpty())
            return;

        // <questionId, questionType>
        Map<Integer, QuestionType> questionTypeMap = new HashMap<>();
        for (SurveyQuestion question : questions) {
            questionTypeMap.put(question.getId(), question.getQuestionType());
        }

        // kazdej odpovedi sa priradzuje typ otazky podla questionId
        for (SurveyResponseAnswerDTO ans : response.getAnswers()) {
            QuestionType type = questionTypeMap.get(ans.getQuestionId());
            // len ak existuje v mape lebo nie vsetky otazky su povinne
            if (type != null) {
                ans.setType(type);
            }
        }
    }

    private void setSurveyResponseChoices(List<SurveyQuestionChoice> surveyQuestionChoices,
                                          SurveyResponseAnswer answer) {
        if (surveyQuestionChoices.isEmpty())
            return;

        List<SurveyResponseChoice> result = new ArrayList<>();
        for (SurveyQuestionChoice choice : surveyQuestionChoices) {
            if (Objects.equals(choice.getSurveyQuestion().getId(), answer.getSurveyQuestion().getId())) {
                SurveyResponseChoice responseChoice = SurveyResponseChoice.builder()
                    .id(new SurveyResponseChoiceId(answer.getId(), choice.getId()))
                    .choice(choice)
                    .surveyResponseAnswer(answer).build();
                result.add(responseChoice);
            }
        }
        answer.setSurveyResponseChoices(result);
    }

    private void validateSurveyQuestionOptionality(List<SurveyResponseAnswerDTO> answers, List<SurveyQuestion> questions) throws ValidationFailureException {
        if (answers == null || answers.isEmpty())
            throw new ValidationFailureException("Answer list cannot be empty.");

        Set<Integer> nonOptionalQuestionsIds = questions.stream()
            .filter(q -> !q.getIsOptional())
            .map(SurveyQuestion::getId)
            .collect(Collectors.toSet());

        Set<Integer> questionIdsFromAnswers = answers.stream()
            .map(SurveyResponseAnswerDTO::getQuestionId)
            .collect(Collectors.toSet());

        nonOptionalQuestionsIds.removeAll(questionIdsFromAnswers);
        if (!nonOptionalQuestionsIds.isEmpty())
            throw new ValidationFailureException("Missing answer for question/s that are non-optional, where Question ids: " + nonOptionalQuestionsIds);
    }

    private void validateResponseAnswer(SurveyResponseAnswerDTO answer) throws ValidationFailureException {
        Optional<SurveyQuestion> surveyQuestionOpt = surveyQuestionRepository.findById(answer.getQuestionId());
        if (surveyQuestionOpt.isEmpty())
            throw new ValidationFailureException("Question with id " + answer.getQuestionId() + " does not exist.");

        SurveyQuestion surveyQuestion = surveyQuestionOpt.get();
        QuestionType type = surveyQuestion.getQuestionType();

        if (type == QuestionType.TEXT
            && (answer.getAnswerContent() == null || answer.getAnswerContent().isEmpty())) {
            throw new ValidationFailureException("QuestionType TEXT does not contain answer - missing content, where questionId: " + surveyQuestion.getId());
        }

        if (type == QuestionType.SCALE) {
            if (answer.getScaleValue() == null)
                throw new ValidationFailureException("QuestionType SCALE does not contain answer - missing scale value, where questionId: " + surveyQuestion.getId());
            if (answer.getScaleValue() < 1 || answer.getScaleValue() > 5)
                throw new ValidationFailureException("QuestionType SCALE has incorrect scale value (min - 1, max - 5), where questionId: " + surveyQuestion.getId());
        }

        if (type == QuestionType.MULTIPLE_CHOICE || type == QuestionType.SINGLE_CHOICE) {

            if (answer.getSurveyResponseChoices() == null || answer.getSurveyResponseChoices().isEmpty())
                throw new ValidationFailureException("QuestionType MULTI/SINGLE CHOICE does not contain answer - missing selected choice ids, where questionId: " + surveyQuestion.getId());

            Set<Integer> existingIds = surveyQuestion.getChoices()
                .stream()
                .map(SurveyQuestionChoice::getId)
                .collect(Collectors.toUnmodifiableSet());

            List<Integer> givenIds = answer.getSurveyResponseChoices().stream()
                .map(SurveyResponseChoiceDTO::getQuestionChoiceId)
                .toList();
            Set<Integer> givenIdsSet = new HashSet<>(givenIds);

            // SINGLE choice can have only one selected choice id
            if (type == QuestionType.SINGLE_CHOICE && givenIds.size() > 1)
                throw new ValidationFailureException("QuestionType SINGLE CHOICE can have only one selected choice id, where questionId: " + surveyQuestion.getId());

            // validate no duplicate Ids in case question is MULTIPLE_CHOICE
            if (givenIdsSet.size() != givenIds.size())
                throw new ValidationFailureException("QuestionType MULTI/SINGLE CHOICE contains duplicate IDs, where questionId: " + surveyQuestion.getId());

            // validate questionChoiceId that was sent in answerDTO actually belongs to given question
            if (!existingIds.containsAll(givenIdsSet))
                throw new ValidationFailureException("QuestionType MULTI/SINGLE CHOICE contains questionChoiceId that does not belong to given Question, where questionId: " + surveyQuestion.getId());
        }
    }

    @Override
    public void delete(Integer id) {
        surveyRepository.deleteById(id);
    }

    @Override
    public SurveyDTO save(SurveyDTO SurveyDTO) {
        Survey survey = surveyMapper.toEntity(SurveyDTO);
        survey = surveyRepository.save(survey);

        return surveyMapper.toDto(survey);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyDTO> findAll(Pageable pageable) {
        return surveyRepository.findAll(pageable).map(surveyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurveyDTO> findOne(Integer id) {
        return surveyRepository.findById(id)
            .map(surveyMapper::toDto);
    }
}
