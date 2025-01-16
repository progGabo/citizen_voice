package sk.tuke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.tuke.domain.Article;
import sk.tuke.domain.UserLikedArticles;



import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserLikedArticlesRepository extends JpaRepository<UserLikedArticles, Long> {
    boolean existsUserLikedArticlesByUserIdAndArticleId(Long userId, Long articleId);

    UserLikedArticles findUserLikedArticlesByUserIdAndArticleId(Long userId, Long articleId);

    List<UserLikedArticles> findUserLikedArticlesByUserId(Long userId);
}
