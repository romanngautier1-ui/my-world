package com.app.myworld.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.myworld.dto.articledto.ArticleCreateRequest;
import com.app.myworld.dto.articledto.ArticleListResponse;
import com.app.myworld.dto.articledto.ArticleResponse;
import com.app.myworld.dto.articledto.ArticleUpdateRequest;
import com.app.myworld.mapper.ArticleMapper;
import com.app.myworld.model.Article;
import com.app.myworld.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final UploadService uploadService;
    private final PdfToHtmlService chapterContentHtmlService;

    @Override
    public List<ArticleListResponse> list() {
        return articleRepository.findAll().stream().map(articleMapper::toList).toList();
    }

    @Override
    public ArticleResponse get(Long id) {
        return articleRepository.findById(id)
                .map(articleMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));
    }

    @Override
    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = articleMapper.toEntity(request);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.toResponse(savedArticle);
    }

    @Override
    @Transactional
    public ArticleResponse update(Long id, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));

        if (request.title() == null && request.content() == null && request.urlImage() == null) {
            throw new IllegalArgumentException("No fields to update");
        }
        if (request.urlImage() != null && !request.urlImage().equals(article.getUrlImage())) {
                String filename = chapterContentHtmlService.extractUploadFilenameFromContent(article.getUrlImage());
                if (filename != null) {
                    uploadService.delete(filename);
                }
            article.setUrlImage(request.urlImage());
        }
        articleMapper.updateEntityFromRequest(request, article);
        return articleMapper.toResponse(article);
    }

    @Override
    public Integer[] getNeighbors(Long id) {
        Article previous = articleRepository.findFirstByIdLessThanOrderByIdDesc(id).orElse(null);
        Article next = articleRepository.findFirstByIdGreaterThanOrderByIdAsc(id).orElse(null);
        return new Integer[] {
                previous != null ? previous.getId().intValue() : null,
                next != null ? next.getId().intValue() : null
        };
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));
        if (article.getUrlImage() != null) {
            String filename = chapterContentHtmlService.extractUploadFilenameFromContent(article.getUrlImage());
            if (filename != null) {
                uploadService.delete(filename);
            }
        }
        articleRepository.delete(article);
    }
}
