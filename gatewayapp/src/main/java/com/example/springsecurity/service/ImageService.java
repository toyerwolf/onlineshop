package com.example.springsecurity.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

     String saveImage(MultipartFile image);
    void changeImage(Long productId, MultipartFile updatedImage) ;
}
