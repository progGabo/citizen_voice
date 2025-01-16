package sk.tuke.service.mapper.specific;

import org.mapstruct.*;
import sk.tuke.domain.Article;
import sk.tuke.domain.Petition;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.dto.specific.PetitionSpecificDTO;
import sk.tuke.service.mapper.EntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetitionSpecificMapper extends EntityMapper<PetitionSpecificDTO, Petition> {
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "city.id", target = "cityId")
    PetitionSpecificDTO toDto(Petition entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Petition partialUpdate(PetitionSpecificDTO petitionSpecificDTO, @MappingTarget Petition petition);
}
