package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.*;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Data
@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {


    @Value("${allowed-file-extension}")
    private List<String> allowedFileExtension;
    private final ProductRepository productRepository;



    public String saveImage(MultipartFile image) {
        String fileName = validateAndCleanFileName(Objects.requireNonNull(image.getOriginalFilename()));
        Path uploadPath = getUploadPath();
        createDirectoriesIfNotExists(uploadPath);
        Path filePath = uploadPath.resolve(fileName);

        copyFile(image, filePath);
        return filePath.getFileName().toString();
    }

    String validateAndCleanFileName(String originalFilename) {
        String[] fileParts = originalFilename.split("\\.");
        String fileExtension = fileParts[fileParts.length - 1];

        if (!allowedFileExtension.contains(fileExtension)) {
            throw new InvalidFileExtensionException("Invalid extension for file");
        }

        return StringUtils.cleanPath(Objects.requireNonNull(originalFilename));
    }

    Path getUploadPath() {
        try {
            return Paths.get(new ClassPathResource("static").getURI());
        } catch (IOException e) {
            throw new UploadPathException("Failed to get upload path");
        }
    }

    void createDirectoriesIfNotExists(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new DirectoryCreationException("Failed to create directories");
            }
        }
    }

    private void copyFile(MultipartFile image, Path filePath) {
        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileCopyException("Failed to copy file");
        }
    }

//    @Override
//    public void changeImage(Long productId, MultipartFile updatedImage)  {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
//        String originalFilename = updatedImage.getOriginalFilename();
//        String[] fileParts = originalFilename.split("\\.");
//        String fileExtension = fileParts[fileParts.length - 1];
//        if (!allowedFileExtension.contains(fileExtension)) {
//            throw new InvalidFileExtensionException("Invalid extension for file");
//        }
//        String fileName = StringUtils.cleanPath(originalFilename);
//        Path uploadPath = Paths.get(new ClassPathResource("static").getURI());
//        Path filePath = uploadPath.resolve(fileName);
//        Files.copy(updatedImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//        product.setImageUrl(originalFilename);
//        productRepository.save(product);
//}

    @Override
    public void changeImage(Long productId, MultipartFile updatedImage) {
        // Шаг 1: Найти продукт по ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

        // Шаг 2: Проверить и очистить имя файла
        String originalFilename = Objects.requireNonNull(updatedImage.getOriginalFilename());
        String fileName = validateAndCleanFileName(originalFilename);

        // Шаг 3: Определить путь для сохранения изображения
        Path uploadPath = getUploadPath();
        Path filePath = uploadPath.resolve(fileName);

        // Шаг 4: Копировать файл
        copyFile(updatedImage, filePath);

        // Шаг 5: Обновить продукт и сохранить в базе данных
        product.setImageUrl(fileName);
        productRepository.save(product);
    }




}
