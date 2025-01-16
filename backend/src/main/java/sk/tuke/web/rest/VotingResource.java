package sk.tuke.web.rest;


import jakarta.validation.Valid;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.startup.StartupTimeMetricsListenerAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.domain.*;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.repository.UserVotingRepository;
import sk.tuke.repository.VotingAnswersRepository;
import sk.tuke.repository.VotingRepository;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.service.VotingService;
import sk.tuke.service.dto.UserVotingDTO;
import sk.tuke.service.dto.VoteAnswersDTO;
import sk.tuke.service.dto.VoteQuestionsDTO;
import sk.tuke.service.dto.VotingDTO;
import sk.tuke.service.dto.create.VotingCreateDTO;
import sk.tuke.service.dto.specific.PetitionSpecificDTO;
import sk.tuke.service.dto.specific.VoteStatisticDTO;
import sk.tuke.service.dto.specific.VotingSpecificDTO;
import sk.tuke.service.mapper.VoteQuestionMapper;
import sk.tuke.service.mapper.VotingMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/voting")
public class VotingResource {


    private final Logger log = LoggerFactory.getLogger(VotingResource.class);
    private final VotingService votingService;
    private final VotingMapper votingMapper;
    private final VotingAnswersRepository votingAnswersRepository;
    private final VoteQuestionMapper voteQuestionMapper;
    private final VotingRepository votingRepository;


    public VotingResource(VotingService votingService, VotingMapper votingMapper
        , UserVotingRepository userVotingRepository
        , VotingAnswersRepository votingAnswersRepository
        , VoteQuestionMapper voteQuestionMapper, VotingRepository votingRepository) {
        this.votingService = votingService;
        this.votingMapper = votingMapper;
        this.votingAnswersRepository = votingAnswersRepository;
        this.voteQuestionMapper = voteQuestionMapper;
        this.votingRepository = votingRepository;
    }


    @PostMapping("/{id}/vote")
    @Secured({AuthoritiesConstants.USER})
    public ResponseEntity<?> vote(@PathVariable Long id, @RequestBody @Valid UserVotingDTO userVotingDTO) {

        if (checkUserVoted(userVotingDTO.getAnswerId().get(0), id)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (votingRepository.hasUserAnsweredVoting(getUserId(),id)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        boolean result = votingService.saveVotings(userVotingDTO, getUserId());
        if (result) {
            List<VoteStatisticDTO> voteStatisticDTO = getStatistics(id);
            return ResponseEntity.ok(voteStatisticDTO);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}/status/change")
    @Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.USER})
    public ResponseEntity<VotingDTO> changeStatus(@PathVariable Long id) {
        Optional<Voting> votingOpt = votingService.findOneEntity(id);

        if (votingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Voting voting = votingOpt.get();
        if (voting.getStatus() == Status.ACTIVE){
            voting.setStatus(Status.INACTIVE);
        }
        else{
            voting.setStatus(Status.ACTIVE);
        }

        VotingDTO result = votingService.save(voting);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/stats")
    @Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.USER})
    public ResponseEntity<?> getStats(@PathVariable Long id){
        List<VoteStatisticDTO> voteStatisticDTO = getStatistics(id);
        if (voteStatisticDTO.isEmpty()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(voteStatisticDTO);
    }

    @PostMapping("/new")
    @Secured({AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<VotingDTO> createVoting(@RequestBody @Valid VotingCreateDTO votingCreateDTO) {
        log.info("Request to create Voting: {}", votingCreateDTO);

        Voting voting = new Voting();
        voting.setVoteQuestions(voteQuestionMapper.toEntity(votingCreateDTO.getVoteQuestions()));
        voting.setStatus(Status.ACTIVE);
        voting.setTitle(votingCreateDTO.getTitle());
        voting.setVoteCount(0);
        voting.setUser(getUserObject());

        for(VotingQuestion question: voting.getVoteQuestions()){
            question.setVoting(voting);
            question.setVoteCount(0);
            question.getVoteAnswers().forEach(ch -> {
                ch.setVoteQuestion(question);
                ch.setVoteCount(0);
            });
        }

        VotingDTO result = votingService.save(voting);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/user/voting")
    @Secured({AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<Page<VotingSpecificDTO>> getAllVotingsByAuthor(Pageable pageable) {

        return ResponseEntity.ok(votingService.findAllByUserId(getUserId() ,pageable));
    }

    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    @PutMapping("/{id}")
    public ResponseEntity<VotingDTO> updateVoting(@PathVariable Long id) {
        log.info("Request to update Voting with ID: {}", id);


        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VotingSpecificDTO> getVoting(@PathVariable Long id) {
        log.info("Request to retrieve Voting with ID: {}", id);

        return votingService.findOneSpec(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/all")
    public ResponseEntity<Page<VotingSpecificDTO>> getAllVotings(Pageable pageable) {
        log.info("Request to retrieve all Votings with pagination");

        Page<VotingSpecificDTO> page = votingService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @Secured({AuthoritiesConstants.ADMIN})
    public ResponseEntity<Void> deleteVoting(@PathVariable Long id) {
        log.info("Request to delete Voting with ID: {}", id);

        votingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean checkUserVoted(Long answerId, Long votingId){
        Optional<VotingAnswers> answer = votingAnswersRepository.findById(answerId);
        if (answer.isEmpty()) {
            return true;
        }
        VotingAnswers answers = answer.get();
        Long answerVotingId = answers.getVoteQuestion().getVoting().getId();
        return !answerVotingId.equals(votingId);
    }

    private Long getUserId () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return user.getId();
    }

    private User getUserObject() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return user;
    }

    private Long getUserCity () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        City city = user.getCity();
        if (city != null) {
            return user.getCity().getId();
        }
        return null;
    }

    private List<VoteStatisticDTO> getStatistics(Long votingId) {
        List<VoteStatisticDTO> voteStatisticDTO = new ArrayList<>();
        Optional<VotingDTO> voting = votingService.findOne(votingId);
        if (voting.isEmpty()) {
            return voteStatisticDTO;
        }
        List<VoteQuestionsDTO> questions = voting.get().getVoteQuestions();

        for (VoteQuestionsDTO question : questions) {
            List<VoteAnswersDTO> answers = question.getVoteAnswers();
            Integer voteCount = question.getVoteCount();
            for (VoteAnswersDTO answer : answers) {
                VoteStatisticDTO statistic = new VoteStatisticDTO();
                statistic.setAnswerId(answer.getId());
                statistic.setVoteCount(answer.getVoteCount());
                statistic.setPercentage(((float)100 / voteCount) * answer.getVoteCount());
                voteStatisticDTO.add(statistic);
            }
        }

        return voteStatisticDTO;
    }

}
