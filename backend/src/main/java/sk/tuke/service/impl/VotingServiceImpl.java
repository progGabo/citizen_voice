package sk.tuke.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.domain.UserVoting;
import sk.tuke.domain.Voting;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.repository.UserVotingRepository;
import sk.tuke.repository.VotingAnswersRepository;
import sk.tuke.repository.VotingQuestionRepository;
import sk.tuke.repository.VotingRepository;
import sk.tuke.service.VotingService;
import sk.tuke.service.dto.UserVotingDTO;
import sk.tuke.service.dto.VotingDTO;
import sk.tuke.service.dto.specific.VotingSpecificDTO;
import sk.tuke.service.mapper.VotingMapper;
import sk.tuke.service.mapper.specific.VotingSpecificMapper;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class VotingServiceImpl implements VotingService {

    private final VotingRepository votingRepository;
    private final VotingMapper votingMapper;
    private final UserVotingRepository userVotingRepository;
    private final VotingAnswersRepository votingAnswersRepository;
    private final VotingQuestionRepository votingQuestionRepository;
    private final VotingSpecificMapper votingSpecificMapper;

    public VotingServiceImpl(VotingRepository votingRepository, VotingMapper votingMapper, UserVotingRepository userVotingRepository,
                             VotingAnswersRepository votingAnswersRepository, VotingQuestionRepository votingQuestionRepository,
                            VotingSpecificMapper votingSpecificMapper) {
        this.votingRepository = votingRepository;
        this.votingMapper = votingMapper;
        this.userVotingRepository = userVotingRepository;
        this.votingAnswersRepository = votingAnswersRepository;
        this.votingQuestionRepository = votingQuestionRepository;
        this.votingSpecificMapper = votingSpecificMapper;
    }

    @Override
    @Transactional
    public Optional<VotingSpecificDTO> findOneSpec(Long id) {
        return votingRepository.findById(id)
            .map(votingSpecificMapper::toDto);
    }

    @Override
    public Optional<Voting> findOneEntity(Long id) {
        return votingRepository.findById(id);
    }

    /**
     * Deletes a voting by its ID.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to deleting voting with ID: {}", id);
        Optional<Voting> voting = votingRepository.findByStatusAndId(Status.ACTIVE,id);
        if (voting.isPresent()) {
            voting.get().setStatus(Status.INACTIVE);
            votingRepository.save(voting.get());
        }
    }

    /**
     * Saves a new voting or updates an existing one.
     *
     * @param votingDTO the entity to save.
     * @return the saved VotingDto.
     */
    @Override
    public VotingDTO save(Voting voting) {
        log.debug("Request to saving voting with details: {}", voting);
        voting = votingRepository.save(voting);

        return votingMapper.toDto(voting);
    }

    @Override
    public Voting saveDTO(VotingDTO votingDTO) {
        log.debug("Request to saving votingDTO: {}", votingDTO);
        return votingRepository.save(votingMapper.toEntity(votingDTO));
    }

    @Override
    public Page<VotingSpecificDTO> findAllByUserId(Long userId, Pageable pageable) {
        return votingRepository.findAllByUserId(userId, pageable).map(votingSpecificMapper::toDto);
    }

    @Override
    public boolean saveVotings(UserVotingDTO userVotingDTO, Long userId) {
        List<Long> votings = userVotingDTO.getAnswerId();

        for(Long votes:votings){
            UserVoting userVoting = new UserVoting();
            userVoting.setVotingAnswerId(votes);
            userVoting.setUserId(userId);
            userVotingRepository.save(userVoting);
        }
        return true;
    }

    /**
     * Finds all votings with pagination.
     *
     * @param pageable the pagination information.
     * @return a paginated list of VotingDTOs.
     */
    @Override
    public Page<VotingSpecificDTO> findAll(Pageable pageable) {
        log.debug("Request to find all votings");
        return votingRepository.findAllByStatus(Status.ACTIVE,pageable)
            .map(votingSpecificMapper::toDto);
    }

    /**
     * Finds a voting by its ID.
     *
     * @param id the entity id
     * @return an Optional containing the VotingDTO if found, otherwise empty.
     */
    @Override
    public Optional<VotingDTO> findOne(Long id) {
        log.debug("Request to find voting with ID: {}", id);

        return votingRepository.findByStatusAndId(Status.ACTIVE, id)
            .map(votingMapper::toDto);
    }


}
