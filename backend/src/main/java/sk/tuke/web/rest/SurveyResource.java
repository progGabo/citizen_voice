package sk.tuke.web.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.domain.survey.Survey;
import sk.tuke.domain.survey.SurveyQuestion;
import sk.tuke.repository.survey.SurveyRepository;
import sk.tuke.repository.survey.SurveyResponseRepository;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.service.SurveyService;
import sk.tuke.service.UserService;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.dto.specific.UserTokenDetails;
import sk.tuke.service.dto.survey.SurveyDTO;
import sk.tuke.service.dto.survey.SurveyResponseDTO;
import sk.tuke.service.dto.survey.SurveyWrapperDTO;
import sk.tuke.service.dto.survey.statistics.SurveyResponseWithStatsDTO;
import sk.tuke.service.dto.survey.statistics.SurveyStatisticsDTO;
import sk.tuke.service.errors.ResourceNotFoundException;
import sk.tuke.service.errors.ValidationFailureException;
import sk.tuke.service.impl.SurveyStatisticsServiceImpl;
import sk.tuke.service.mapper.survey.SurveyMapper;
import sk.tuke.service.mapper.survey.SurveyResponseMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/survey")
public class SurveyResource {
    private static final Logger log = LoggerFactory.getLogger(SurveyResource.class);
    private static final String ENTITY_NAME = "Survey";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final SurveyService surveyService;

    private final SurveyMapper surveyMapper;

    private final SurveyRepository surveyRepository;

    private SurveyStatisticsServiceImpl surveyStatisticsService;
    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyResponseMapper surveyResponseMapper;

    private final UserService userService;

    public SurveyResource(SurveyService surveyService,
                          SurveyMapper surveyMapper,
                          SurveyRepository surveyRepository,
                          SurveyStatisticsServiceImpl surveyStatisticsService,
                          SurveyResponseRepository surveyResponseRepository,
                          SurveyResponseMapper surveyResponseMapper,
                          UserService userService){
        this.surveyService = surveyService;
        this.surveyMapper = surveyMapper;
        this.surveyRepository = surveyRepository;
        this.surveyStatisticsService = surveyStatisticsService;
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyResponseMapper = surveyResponseMapper;
        this.userService = userService;
    }

    @PostMapping("/new")
    @Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<SurveyDTO> createSurvey(@Valid @RequestBody SurveyDTO request) {
        log.info("Request to create Survey: {}", request);
        Survey survey = surveyMapper.toEntity(request);
        survey.setAuthor(getUser(getUserId()));
        survey.setStatus(Status.ACTIVE);

        // set parent entities to child ones
        for (SurveyQuestion question : survey.getSurveyQuestions()) {
            question.setSurvey(survey);
            question.getChoices().forEach(ch -> {
                ch.setSurveyQuestion(question);
            });
        }

        surveyRepository.save(survey);
        return ResponseEntity.status(HttpStatus.CREATED).body(surveyMapper.toDto(survey));
    }

    @PostMapping("/{surveyId}/answer")
    @Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity answerSurvey(@Valid @RequestBody SurveyResponseDTO response,
                                                  @PathVariable Integer surveyId) {
        log.info("Request to answer survey with id: {}", surveyId);

        if (!surveyRepository.existsById(surveyId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        response.setSurveyId(surveyId);
        response.setRespondent(getUserInfo(getUserId()));

        // only one answer allowed per user
        if (surveyResponseRepository.existsBySurveyIdAndRespondentId(surveyId, getUserId()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        if (surveyRepository.findById(surveyId).get().getStatus() != Status.ACTIVE)
            return ResponseEntity.status(HttpStatus.LOCKED).build();

        try {
            SurveyResponseDTO processedResponse = surveyService.processResponse(response);
            SurveyStatisticsDTO stats = surveyStatisticsService.getStatisticsForSurvey(surveyId);
            SurveyResponseWithStatsDTO res = new SurveyResponseWithStatsDTO();
            res.setResponse(processedResponse);
            res.setStatistics(stats);
            return ResponseEntity.ok(res);
        } catch (ResourceNotFoundException | ValidationFailureException e) {
            log.debug("Survey answering failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private UserInfoDTO getUserInfo(Long userId) {
        return userService.findUserById(userId).get();
    }

    private User getUser(Long userId) {
        return userService.findUserByIdSpecific(userId).get();
    }


    @GetMapping("/all")
    public ResponseEntity<Page<SurveyDTO>> getAllSurveys(Pageable pageable) {
        log.debug("REST request to get all Surveys");
        Page<SurveyDTO> page = surveyService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/all/user")
    @Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<List<SurveyWrapperDTO>> getAllSurveysByUser() {
        log.debug("REST request to get all Surveys made by same user with count of respondents");
        List<SurveyWrapperDTO> res = surveyService.findAllbyUser(getUserId().intValue());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDTO> getSurvey(@PathVariable Integer id) {
        log.info("Request to retrieve Survey with ID: {}", id);

        return surveyService.findOne(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/stats/{id}")
    public ResponseEntity<SurveyStatisticsDTO> getSurveyStats(@PathVariable Integer id) {
        log.info("Request to retrieve Survey statistics for survey with ID: {}", id);
        try {
            SurveyStatisticsDTO result = surveyStatisticsService.getStatisticsForSurvey(id);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (ResourceNotFoundException e) {
            log.debug("Fetching survey stats failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/all-answers")
    public ResponseEntity<Page<SurveyResponseDTO>> getSurveyAnswers(Pageable pageable,
                                                                    @PathVariable Integer id) {
        log.info("Request to retrieve Survey answers for survey with ID: {}", id);
        Page<SurveyResponseDTO> result = surveyResponseRepository.findBySurveyId(pageable, id)
            .map(surveyResponseMapper::toDto);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/{id}/status")
    @Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<SurveyDTO> changeSurveyStatus(@PathVariable Integer id) {
        log.info("Request to change status of Survey with ID: {}", id);
        Optional<Survey> surveyOptional = surveyRepository.findById(id);

        if (surveyOptional.isEmpty())
            return ResponseEntity.notFound().build();

        Survey survey = surveyOptional.get();

        if (!Objects.equals(survey.getAuthor().getId(), getUserId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (survey.getStatus() == Status.ACTIVE) {
            survey.setStatus(Status.INACTIVE);
        } else if (survey.getStatus() == Status.INACTIVE) {
            survey.setStatus(Status.ACTIVE);
        }

        SurveyDTO res = surveyMapper.toDto(surveyRepository.save(survey));
        return ResponseEntity.ok(res);
    }



    private Long getUserId () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return user.getId();
    }

}
