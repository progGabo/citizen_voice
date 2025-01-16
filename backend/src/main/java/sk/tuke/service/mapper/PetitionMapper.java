package sk.tuke.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sk.tuke.domain.Article;
import sk.tuke.domain.Petition;
import sk.tuke.domain.Petition;
import sk.tuke.service.dto.PetitionDTO;
import sk.tuke.service.dto.PetitionDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetitionMapper extends EntityMapper<PetitionDTO, Petition> {

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "cityId", target = "city.id")
    Petition toEntity(PetitionDTO petitionDTO);


    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cityId", source = "city.id")
    PetitionDTO toDto(Petition petition);
}
