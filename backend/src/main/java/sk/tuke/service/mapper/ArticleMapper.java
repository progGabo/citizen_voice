package sk.tuke.service.mapper;

import org.mapstruct.*;
import sk.tuke.domain.Article;
import sk.tuke.service.dto.ArticleDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArticleMapper extends EntityMapper<ArticleDTO, Article> {

    @Mapping(source = "cityId", target = "city.id")
    @Mapping(source = "userId", target = "user.id")
    Article toEntity(ArticleDTO dto);

    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstname")
    @Mapping(source = "user.lastName", target = "lastname")
    ArticleDTO toDto(Article entity);
}
