package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Product;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.service.ImageService;
import lombok.AllArgsConstructor;
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

@Getter
@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Setter
    @Value("${allowed-file-extension}")
    private List<String> allowedFileExtension;
    private final ProductRepository productRepository;



    public String saveImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String[] fileParts = originalFilename.split("\\.");
        String fileExtension = fileParts[fileParts.length - 1];

        if (allowedFileExtension.contains(fileExtension)) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(originalFilename));
            Path uploadPath = Paths.get(new ClassPathResource("static").getURI());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.getFileName().toString();
        } else {
            throw new RuntimeException("Invalid extension for file");
        }
    }

    @Override
    public void changeImage(Long productId, MultipartFile updatedImage) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        String originalFilename = updatedImage.getOriginalFilename();
        String[] fileParts = originalFilename.split("\\.");
        String fileExtension = fileParts[fileParts.length - 1];
        if (!allowedFileExtension.contains(fileExtension)) {
            throw new RuntimeException("Invalid extension for file");
        }
        String fileName = StringUtils.cleanPath(originalFilename);
        Path uploadPath = Paths.get(new ClassPathResource("static").getURI());
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(updatedImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        product.setImageUrl(originalFilename);
        productRepository.save(product);
}


}
