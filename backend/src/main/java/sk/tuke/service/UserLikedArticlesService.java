package sk.tuke.service;

import org.springframework.data.domain.Page;
import sk.tuke.domain.Article;
import sk.tuke.domain.UserLikedArticles;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public interface UserLikedArticlesService {
    UserLikedArticles save(UserLikedArticles userLikedArticles);

    boolean exists(Long userId, Long articleId);

    UserLikedArticles delete(Long userId, Long articleId);

    List<UserLikedArticles> findAllByUserId(Long userId);
}
