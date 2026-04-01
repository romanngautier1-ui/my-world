package com.app.myworld.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.app.myworld.dto.bookdto.BookCreateRequest;
import com.app.myworld.dto.bookdto.BookListResponse;
import com.app.myworld.dto.bookdto.BookResponse;
import com.app.myworld.dto.bookdto.BookSummaryResponse;
import com.app.myworld.dto.bookdto.BookUpdateRequest;
import com.app.myworld.dto.chapterdto.ChapterListResponse;
import com.app.myworld.model.Book;
import com.app.myworld.model.Chapter;

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.ERROR,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BookMapper {

	BookSummaryResponse toSummary(Book book);

	@Mapping(target = "chapterCount", expression = "java(book.getChapters() == null ? 0L : (long) book.getChapters().size())")
	BookListResponse toList(Book book);

	@Mapping(target = "chapters", source = "chapters", qualifiedByName = "chaptersToList")
	BookResponse toResponse(Book book);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "chapters", ignore = true)
	Book toEntity(BookCreateRequest request);

	ChapterListResponse toChapterList(Chapter chapter);

	@Named("chaptersToList")
	default List<ChapterListResponse> toChapterListResponseList(List<Chapter> chapters) {
		if (chapters == null || chapters.isEmpty()) {
			return List.of();
		}
		List<ChapterListResponse> result = new ArrayList<>(chapters.size());
		for (Chapter chapter : chapters) {
			result.add(toChapterList(chapter));
		}
		return result;
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "chapters", ignore = true)
	void updateEntityFromRequest(BookUpdateRequest request, @MappingTarget Book book);
}
