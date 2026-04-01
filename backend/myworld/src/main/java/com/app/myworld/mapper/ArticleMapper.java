package com.app.myworld.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.myworld.dto.articledto.ArticleCreateRequest;
import com.app.myworld.dto.articledto.ArticleListResponse;
import com.app.myworld.dto.articledto.ArticleResponse;
import com.app.myworld.dto.articledto.ArticleUpdateRequest;
import com.app.myworld.model.Article;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE
)
public interface ArticleMapper {
    
    ArticleListResponse toList(Article article);

    /** Entity → ResponseDTO (lecture seule, mapping direct car champs identiques). */
    @Mapping(source = "user.username", target = "username")
    ArticleResponse toResponse(Article article);

    @Mapping(target = "id", ignore= true)
    @Mapping(target="createdAt", ignore= true)
    @Mapping(target= "user.id", source= "userId")
    Article toEntity(ArticleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequest(ArticleUpdateRequest request, @MappingTarget Article article);
}
