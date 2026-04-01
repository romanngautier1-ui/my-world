package com.app.myworld.service;

import java.util.List;

import com.app.myworld.dto.articledto.ArticleCreateRequest;
import com.app.myworld.dto.articledto.ArticleListResponse;
import com.app.myworld.dto.articledto.ArticleResponse;
import com.app.myworld.dto.articledto.ArticleUpdateRequest;

public interface ArticleService {
    
    List<ArticleListResponse> list();

    ArticleResponse get(Long id);

    ArticleResponse create(ArticleCreateRequest request);

    ArticleResponse update(Long id, ArticleUpdateRequest request);

    Integer[] getNeighbors(Long id);

    void delete(Long id);
}
