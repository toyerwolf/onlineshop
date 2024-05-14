package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.InvalidFileExtensionException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private ProductRepository productRepository;


    @Test
    public void testSaveImage_Successful() throws IOException {
        // Подготовка данных для теста
        List<String> allowedFileExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        ImageServiceImpl imageService = new ImageServiceImpl(allowedFileExtensions, mock(ProductRepository.class));
        byte[] fileContent = "test data".getBytes();
        String originalFilename = "test.jpg";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", originalFilename, "image/jpeg", fileContent);

        // Вызов метода, который тестируем
        String savedFileName = imageService.saveImage(mockMultipartFile);

        // Получение пути, по которому должен быть сохранен файл
        Path uploadPath = Paths.get(new ClassPathResource("static").getURI());
        Path savedFilePath = uploadPath.resolve(savedFileName);

        // Проверки
        assertNotNull(savedFileName); // Успешное сохранение (имя файла не null)

        // Проверка, что файл был создан с верным расширением
        String savedFileExtension = savedFileName.substring(savedFileName.lastIndexOf('.') + 1);
        assertTrue(allowedFileExtensions.contains(savedFileExtension));

        // Проверка, что файл был создан в указанном месте
        assertTrue(Files.exists(savedFilePath));

        // Проверка, что файл был создан с верным содержимым
        byte[] savedFileContent = Files.readAllBytes(savedFilePath);
        assertArrayEquals(fileContent, savedFileContent);

        // Удаление созданного файла после теста
        Files.delete(savedFilePath);
    }

    @Test
    public void testSaveImage_InvalidExtension() throws IOException {
        List<String> allowedFileExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        byte[] fileContent = "test data".getBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "test.txt", "text/plain", fileContent);
        ImageServiceImpl imageService = new ImageServiceImpl(allowedFileExtensions, mock(ProductRepository.class));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> imageService.saveImage(mockMultipartFile));
        assertEquals("Invalid extension for file", exception.getMessage());
    }

    @Test
    void testChangeImage_Successful() throws IOException {
        // Подготовка данных для теста
        Long productId = 1L;
        byte[] fileContent = "updated image data".getBytes();
        String originalFilename = "updated_image.jpg";
        MultipartFile updatedImage = new MockMultipartFile("image", originalFilename, "image/jpeg", fileContent);
        Product product = new Product();
        product.setId(productId);

        // Mock
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        List<String> allowedFileExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        imageService.setAllowedFileExtension(allowedFileExtensions);

        // Вызов метода, который тестируем
        imageService.changeImage(productId, updatedImage);
        //место где будет сохранен файл
        Path uploadPath = Paths.get(new ClassPathResource("static").getURI());
        Path savedFilePath = uploadPath.resolve(originalFilename);
        //существует ли файл по этому пути
        assertTrue(Files.exists(savedFilePath));


      //считываем содержимое сохраненного файла в массив байтов
        byte[] savedFileContent = Files.readAllBytes(savedFilePath);
        assertArrayEquals(fileContent, savedFileContent);

        // Проверяем изображениe в объекте Product был обновлен корректно.
        assertEquals(originalFilename, product.getImageUrl());
        verify(productRepository, times(1)).save(product);


        Files.delete(savedFilePath);
    }

    @Test
    void testChangeImage_ProductNotFound() {
        Long nonExistingProductId = 999L;
        MultipartFile updatedImage = new MockMultipartFile("image", "updated_image.jpg", "image/jpeg", "updated image data".getBytes());

        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> imageService.changeImage(nonExistingProductId, updatedImage)
        );


        assertEquals("Product not found with id: " + nonExistingProductId, exception.getMessage());
    }

    @Test
    void testChangeImage_InvalidFileExtension() {
        // Подготовка данных для теста
        Long productId = 1L;
        byte[] fileContent = "updated image data".getBytes();
        String originalFilename = "updated_image.txt";
        MultipartFile updatedImage = new MockMultipartFile("image", originalFilename, "text/plain", fileContent);
        Product product = new Product();
        product.setId(productId);
        List<String> allowedFileExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        ImageServiceImpl imageService = new ImageServiceImpl(allowedFileExtensions, productRepository);
        // Mock
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Вызов метода, который тестируем
        InvalidFileExtensionException exception = assertThrows(
                InvalidFileExtensionException.class,
                () -> imageService.changeImage(productId, updatedImage)
        );

        // Проверка генерации исключения с правильным сообщением
        assertEquals("Invalid extension for file", exception.getMessage());
    }

}