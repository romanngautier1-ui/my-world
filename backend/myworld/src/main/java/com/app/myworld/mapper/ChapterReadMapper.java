package com.app.myworld.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.myworld.dto.chapterreaddto.ChapterReadCreateRequest;
import com.app.myworld.dto.chapterreaddto.ChapterReadResponse;
import com.app.myworld.model.ChapterRead;

@Mapper(componentModel = "spring")
public interface ChapterReadMapper {
    
    @Mapping(source = "chapter.title", target = "title")
    @Mapping(source = "chapter.number", target = "number")
    ChapterReadResponse toResponse(ChapterRead chapterRead);

    @Mapping(target = "id", ignore= true)
    @Mapping(target="readAt", ignore= true)
    @Mapping(target= "user.id", source= "userId")
    @Mapping(target= "chapter.id", source= "chapterId")
    ChapterRead toEntity(ChapterReadCreateRequest request);
}
