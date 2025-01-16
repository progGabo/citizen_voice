package sk.tuke.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;
import sk.tuke.domain.Article;
import sk.tuke.domain.UserLikedArticles;
import sk.tuke.repository.UserLikedArticlesRepository;
import sk.tuke.service.UserLikedArticlesService;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class UserLikedArticlesServiceImpl implements UserLikedArticlesService {

    private final UserLikedArticlesRepository userLikedArticlesRepository;

    public UserLikedArticlesServiceImpl(UserLikedArticlesRepository userLikedArticlesRepository) {
        this.userLikedArticlesRepository = userLikedArticlesRepository;
    }
    @Override
    public UserLikedArticles save(UserLikedArticles userLikedArticles) {
        return userLikedArticlesRepository.save(userLikedArticles);
    }

    @Override
    public boolean exists(Long userId, Long articleId) {
        return userLikedArticlesRepository.existsUserLikedArticlesByUserIdAndArticleId(userId, articleId);
    }

    @Override
    public UserLikedArticles delete(Long userId, Long articleId) {
        UserLikedArticles result = userLikedArticlesRepository.findUserLikedArticlesByUserIdAndArticleId(userId, articleId);
        userLikedArticlesRepository.delete(result);
        return result;
    }

    @Override
    public List<UserLikedArticles> findAllByUserId(Long userId) {
        return userLikedArticlesRepository.findUserLikedArticlesByUserId(userId);
    }


}
