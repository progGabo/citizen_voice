package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.Article;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByCityId(Pageable pageable, Long cityId);

    Optional<Article> findByTitle(String title);

    Page<Article> findAllByStatus(Status status, Pageable pageable);

    Page<Article> findAllByUserId(Long id, Pageable pageable);
}
