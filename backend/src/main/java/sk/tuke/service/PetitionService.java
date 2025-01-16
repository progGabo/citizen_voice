package sk.tuke.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.tuke.domain.Petition;
import sk.tuke.domain.Signee;
import sk.tuke.domain.User;
import sk.tuke.service.dao.PetitionSigneesDAO;
import sk.tuke.service.dto.PetitionDTO;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.dto.specific.PetitionSigneesDTO;
import sk.tuke.service.dto.specific.PetitionSpecificDTO;
import sk.tuke.service.dto.specific.SignedPetitionDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PetitionService {

    /**
     * Delete the "id" Petition.
     *
     * @param id the id of the entity.
     */
    void delete(Integer id);

    void setDelete(PetitionDTO petition);

    /**
     * Save a Petition.
     *
     * @param petitionDTO the entity to save.
     * @return the persisted entity.
     */
    PetitionDTO save(PetitionDTO petitionDTO);

    /**
     * Get all the petitions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PetitionSpecificDTO> findAll(Pageable pageable);

    Page<PetitionSpecificDTO> findAllActive(Pageable pageable);

    Page<PetitionSpecificDTO> findAllByAuthorId(Long id, Pageable pageable);
    /**
     * Gets the Petition by id.
     *
     * @param id the entity id
     * @return Optional with found entity
     * or {@link Optional#EMPTY} if not found
     */
    Optional<PetitionDTO> findOne(Integer id);

    boolean sign(PetitionDTO petitionDTO, Long userId);

    public boolean finishSigning(Signee signee);

    List<SignedPetitionDTO> getAllSignedByUserId(Long id);

    List<PetitionSigneesDTO> getAllPetitionSignees(Integer petitionId);
}
