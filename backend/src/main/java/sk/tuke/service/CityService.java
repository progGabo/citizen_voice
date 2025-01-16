package sk.tuke.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.tuke.service.dto.ArticleDTO;
import sk.tuke.service.dto.CityDTO;
import sk.tuke.service.dto.specific.CitySpecificDTO;

import java.util.Optional;

public interface CityService {

    Page<CitySpecificDTO> findAll(Pageable pageable);

    Optional<CitySpecificDTO> findOneById(Long id);
}
