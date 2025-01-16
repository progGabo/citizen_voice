package sk.tuke.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.tuke.domain.Article;
import sk.tuke.service.dto.ArticleDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.dto.specific.PetitionSpecificDTO;

import java.util.Optional;

public interface ArticleService {


    void delete(Long id);

    ArticleDTO save(ArticleDTO ArticleDTO);

    Page<ArticleSpecificDTO> findAll(Pageable pageable);

    Optional<ArticleSpecificDTO> findOneSpec(Long id);

    Optional<ArticleDTO> findOne(Long id);

    Page<ArticleSpecificDTO> findAllByUserId(Long id, Pageable pageable);

    Page<ArticleSpecificDTO> findAllByCity(Pageable pageable, Long city_id);

    void setDelete(ArticleDTO article);

    Page<ArticleSpecificDTO> findAllActive(Pageable pageable);

    Optional<Article> findOneEntity(Long id);
}
