package com.example.springsecurity.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

     String saveImage(MultipartFile image) throws IOException;
    void changeImage(Long productId, MultipartFile updatedImage) throws IOException;
}
