package sk.tuke.web.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.domain.Article;
import sk.tuke.domain.City;
import sk.tuke.domain.User;
import sk.tuke.domain.UserLikedArticles;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.service.dto.ArticleDTO;
import sk.tuke.service.dto.create.ArticleCreateDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.dto.specific.PetitionSpecificDTO;
import sk.tuke.service.impl.ArticleServiceImpl;
import sk.tuke.service.impl.UserLikedArticlesServiceImpl;
import sk.tuke.service.mapper.ArticleMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/article")
public class ArticleResource {

    private static final Logger log = LoggerFactory.getLogger(ArticleResource.class);
    private static final String ENTITY_NAME = "Article";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final ArticleServiceImpl articleService;
    private final UserLikedArticlesServiceImpl likedArticlesService;
    private final ArticleMapper articleMapper;
    public ArticleResource(ArticleServiceImpl articleService,UserLikedArticlesServiceImpl likedArticlesService, ArticleMapper articleMapper) {
        this.articleService = articleService;
        this.likedArticlesService = likedArticlesService;
        this.articleMapper = articleMapper;
    }

    @GetMapping("/user/articles")
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<Page<ArticleSpecificDTO>> getAllArticlesByAuthor(Pageable pageable) {

        return ResponseEntity.ok(articleService.findAllByUserId(getUserId() ,pageable));
    }


    @PostMapping("/user/like/{id}")
    @Secured({AuthoritiesConstants.USER})
    public ResponseEntity<UserLikedArticles> likeUnlikeArticle(@PathVariable Long id){
        UserLikedArticles userLikedArticles = new UserLikedArticles();
        Long userId = getUserId();

        if (likedArticlesService.exists(userId,id))
            return ResponseEntity.ok(likedArticlesService.delete(userId,id));

        userLikedArticles.setUserId(userId);
        Optional<Article> article = articleService.findOneEntity(id);
        if (article.isEmpty())
            return ResponseEntity.badRequest().build();

        userLikedArticles.setArticle(article.get());
        UserLikedArticles result = likedArticlesService.save(userLikedArticles);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status/change")
    @Secured({AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<ArticleDTO> changeStatus(@PathVariable Long id){
        Optional<Article> article = articleService.findOneEntity(id);
        if (article.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Article article1 = article.get();
        if (article1.getStatus() == Status.ACTIVE) {
            article1.setStatus(Status.INACTIVE);
        }
        else{
            article1.setStatus(Status.ACTIVE);
        }

        ArticleDTO result = articleService.save(articleMapper.toDto(article1));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/get/liked")
    @Secured({AuthoritiesConstants.USER})
    public ResponseEntity<Page<ArticleDTO>> getLikedArticles(){
        List<UserLikedArticles> articles = likedArticlesService.findAllByUserId(getUserId());
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        for (UserLikedArticles UsersArticle : articles){
            Article article = UsersArticle.getArticle();
            ArticleDTO articleDTO = articleMapper.toDto(article);
            articleDTOs.add(articleDTO);
        }

        Page<ArticleDTO> result = new PageImpl<>(articleDTOs);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/new")
    @Secured({AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<ArticleDTO> createArticle(@Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        log.info("Request to create Article: {}", articleCreateDTO);
        ArticleDTO articleDTO = new ArticleDTO();

        Long user = getUserId();
        Long city = getUserCity();

        articleDTO.setStatus(Status.ACTIVE);
        articleDTO.setTitle(articleCreateDTO.getTitle());
        articleDTO.setContent(articleCreateDTO.getContent());
        articleDTO.setPublishDate(articleCreateDTO.getPublishDate());
        articleDTO.setUserId(user);
        articleDTO.setLikes(0);
        articleDTO.setCityId(city);

        ArticleDTO result = articleService.save(articleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ArticleSpecificDTO>> getAllArticles(Pageable pageable) {
        log.debug("REST request to get all Articles");
        Page<ArticleSpecificDTO> page = articleService.findAllActive(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleSpecificDTO> getArticle(@PathVariable Long id) {
        log.info("Request to retrieve Article with ID: {}", id);

        return articleService.findOneSpec(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Secured({AuthoritiesConstants.ORGANIZATION})
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id,@RequestBody ArticleSpecificDTO updated) {
        log.info("Request to update Article with ID: {}", id);

        Optional<ArticleDTO> articleOpt = articleService.findOne(id);

        if (articleOpt.isEmpty())
            return ResponseEntity.badRequest().build();

        Long userId = getUserId();

        ArticleDTO article = articleOpt.get();
        if (userId != article.getUserId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        article.setContent(updated.getContent());
        article.setTitle(updated.getTitle());

        ArticleDTO result = articleService.save(article);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/city/{city_id}")
    public ResponseEntity<Page<ArticleSpecificDTO>> getAllArticles(Pageable pageable, @PathVariable Long city_id) {
        log.debug("REST request to get all Articles by city");
        Page<ArticleSpecificDTO> page = articleService.findAllByCity(pageable, city_id);
        return ResponseEntity.ok(page);
    }

    @Secured({AuthoritiesConstants.ORGANIZATION})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePetition(@PathVariable Long id) {
        log.info("Request to delete Article with ID: {}", id);

        Optional<ArticleDTO> articleOpt = articleService.findOne(id);

        if (articleOpt.isEmpty())
            return ResponseEntity.badRequest().build();

        Long userId = getUserId();
        ArticleDTO article = articleOpt.get();
        if (userId != article.getUserId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        articleService.setDelete(article);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return user.getId();
    }

    private Long getUserCity () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        City city = user.getCity();
        if (city != null) {
            return user.getCity().getId();
        }
        return null;
    }
}
