package com.app.myworld.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.app.myworld.dto.userdto.UserChapterReadResponse;
import com.app.myworld.dto.userdto.UserCreateRequest;
import com.app.myworld.dto.userdto.UserResponse;
import com.app.myworld.dto.userdto.UserUpdateRequest;
import com.app.myworld.model.ChapterRead;
import com.app.myworld.model.User;

@Mapper(
	componentModel = "spring",
    // builder = @Builder(disableBuilder = true),
	unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy= NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

	@Mapping(source = "chapterReads", target = "lastReads")
	UserResponse toResponse(User user);

	@Mapping(source = "chapter.id", target = "chapterId")
	@Mapping(source = "chapter.title", target = "chapterTitle")
	@Mapping(source = "chapter.book.number", target = "bookNumber")
	UserChapterReadResponse toChapterReadResponse(ChapterRead read);

    default List<UserChapterReadResponse> toChapterReadResponseList(Set<ChapterRead> reads) {
        if (reads == null || reads.isEmpty()) {
            return List.of();
        }
        List<UserChapterReadResponse> result = new ArrayList<>(reads.size());
        for (ChapterRead read : reads) {
            result.add(toChapterReadResponse(read));
        }
        return result;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "chapterReads", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiresAt", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    // @Mapping(target = "authorities", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "chapterReads", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiresAt", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateEntityFromRequest(UserUpdateRequest request, @MappingTarget User user);
}
