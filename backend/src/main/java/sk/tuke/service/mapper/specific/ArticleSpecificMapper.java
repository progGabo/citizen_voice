package sk.tuke.service.mapper.specific;

import org.mapstruct.*;
import sk.tuke.domain.Article;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.mapper.EntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArticleSpecificMapper extends EntityMapper<ArticleSpecificDTO, Article>  {

    @Mapping(source = "user.firstName", target = "firstname")
    @Mapping(source = "user.lastName", target = "lastname")
    ArticleSpecificDTO toDto(Article entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Article partialUpdate(ArticleSpecificDTO articleSpecificDTO, @MappingTarget Article article);
}
