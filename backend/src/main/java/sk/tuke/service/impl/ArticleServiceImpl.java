package sk.tuke.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.domain.Article;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.repository.ArticleRepository;
import sk.tuke.service.ArticleService;
import sk.tuke.service.dto.ArticleDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.mapper.ArticleMapper;
import tech.jhipster.config.JHipsterProperties;
import sk.tuke.service.mapper.specific.ArticleSpecificMapper;

import java.util.Optional;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    private final ArticleMapper articleMapper;

    private final ArticleSpecificMapper articleSpecificMapper;

    private final JHipsterProperties jHipsterProperties;

    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleMapper articleMapper, JHipsterProperties jHipsterProperties
                                ,ArticleSpecificMapper articleSpecificMapper) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.jHipsterProperties = jHipsterProperties;
        this.articleSpecificMapper = articleSpecificMapper;
    }

    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public ArticleDTO save(ArticleDTO ArticleDTO) {
        Article article = articleMapper.toEntity(ArticleDTO);
        article = articleRepository.save(article);

        return articleMapper.toDto(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSpecificDTO> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable).map(articleSpecificMapper::toDto);
    }


    @Transactional(readOnly = true)
    public Page<ArticleSpecificDTO> findAllActive(Pageable pageable) {
        return articleRepository.findAllByStatus(Status.ACTIVE, pageable)
            .map(articleSpecificMapper::toDto);
    }

    @Override
    public Optional<Article> findOneEntity(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArticleSpecificDTO> findOneSpec(Long id) {
        return articleRepository.findById(id)
            .map(articleSpecificMapper::toDto);
    }

    @Override
    public Optional<ArticleDTO> findOne(Long id) {
        return articleRepository.findById(id)
            .map(articleMapper::toDto);
    }

    @Override
    public Page<ArticleSpecificDTO> findAllByUserId(Long id, Pageable pageable) {
        return articleRepository.findAllByUserId(id,pageable).map(articleSpecificMapper::toDto);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<ArticleSpecificDTO> findAllByCity(Pageable pageable, Long city_id) {
        return articleRepository.findAllByCityId(pageable, city_id).map(articleSpecificMapper::toDto);
    }

    @Override
    public void setDelete(ArticleDTO article) {
        article.setStatus(Status.DELETED);
        articleRepository.save(articleMapper.toEntity(article));
        articleRepository.flush();
    }
}
