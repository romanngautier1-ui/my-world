package com.app.myworld.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    
    String saveImage(MultipartFile file);

    String savePdf(MultipartFile file);

    Resource loadAsResource(String filename);

    void delete(String filename);
}
