package com.app.myworld.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.app.myworld.dto.chapterdto.ChapterCreateRequest;
import com.app.myworld.dto.chapterdto.ChapterListResponse;
import com.app.myworld.dto.chapterdto.ChapterResponse;
import com.app.myworld.dto.chapterdto.ChapterUpdateRequest;
import com.app.myworld.model.Chapter;

@Mapper(
    componentModel="spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy= NullValuePropertyMappingStrategy.IGNORE,
    uses = {CommentMapper.class, BookMapper.class}
)
public interface ChapterMapper {

    ChapterListResponse toList(Chapter chapter);

    @Mapping(source = "likeCount", target = "like")
    ChapterResponse toResponse(Chapter chapter);

    @Mapping(target="id", ignore=true)
    @Mapping(target="createdAt", ignore=true)
    @Mapping(target="likeCount", ignore=true)
    @Mapping(target="comments", ignore=true)
    @Mapping(target="reads", ignore=true)
    @Mapping(target="book.id", source="bookId")
    Chapter toEntity(ChapterCreateRequest request);

    @Mapping(target="id", ignore=true)
    @Mapping(target="createdAt", ignore=true)
    @Mapping(target="likeCount", ignore=true)
    @Mapping(target="comments", ignore=true)
    @Mapping(target="reads", ignore=true)
    @Mapping(target="book.id", source="bookId")
    void updateEntityFromRequest(ChapterUpdateRequest request, @MappingTarget Chapter chapter);
}
