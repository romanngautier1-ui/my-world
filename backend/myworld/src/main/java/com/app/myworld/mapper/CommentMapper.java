package com.app.myworld.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.myworld.dto.commentdto.CommentCreateRequest;
import com.app.myworld.dto.commentdto.CommentResponse;
import com.app.myworld.dto.commentdto.CommentUpdateRequest;
import com.app.myworld.model.Comment;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {

    @Mapping(source = "user.username", target = "username")
    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toList(List<Comment> comments);

    @Mapping(source = "userId",    target = "user.id")
    @Mapping(source = "chapterId", target = "chapter.id")
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentCreateRequest request);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user",      ignore = true)
    @Mapping(target = "chapter",   ignore = true)
    void updateEntityFromRequest(CommentUpdateRequest request, @MappingTarget Comment comment);
}