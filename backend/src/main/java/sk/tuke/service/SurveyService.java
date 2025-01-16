package sk.tuke.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.tuke.service.dto.survey.SurveyDTO;
import sk.tuke.service.dto.survey.SurveyResponseDTO;
import sk.tuke.service.dto.survey.SurveyWrapperDTO;
import sk.tuke.service.errors.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface SurveyService {
    void delete(Integer id);

    SurveyDTO save(SurveyDTO SurveyDTO);

    Page<SurveyDTO> findAll(Pageable pageable);

    List<SurveyWrapperDTO> findAllbyUser(Integer authorId);

    Optional<SurveyDTO> findOne(Integer id);

    SurveyResponseDTO processResponse(SurveyResponseDTO response) throws ResourceNotFoundException;
}
