package sk.tuke.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.tuke.domain.Voting;
import sk.tuke.service.dto.UserVotingDTO;
import sk.tuke.service.dto.VotingDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.dto.specific.VotingSpecificDTO;

import java.util.Optional;

public interface VotingService {

    /**
     * Delete the "id" Voting.
     *
     * @param id the id of the entity.
     */
    Optional<Voting> findOneEntity(Long id);
    void delete(Long id);

    /**
     * Save a Voting.
     *
     * @param VotingDTO the entity to save.
     * @return the persisted entity.
     */
    VotingDTO save(Voting voting);

    Voting saveDTO(VotingDTO votingDTO);

    Page<VotingSpecificDTO> findAllByUserId(Long userId, Pageable pageable);

    boolean saveVotings(UserVotingDTO userVotingDTO, Long userId);
    /**
     * Get all the Votings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VotingSpecificDTO> findAll(Pageable pageable);

    /**
     * Gets the Voting by id.
     *
     * @param id the entity id
     * @return Optional with found entity
     * or {@link Optional#EMPTY} if not found
     */
    Optional<VotingDTO> findOne(Long id);


    Optional<VotingSpecificDTO> findOneSpec(Long id);
}
