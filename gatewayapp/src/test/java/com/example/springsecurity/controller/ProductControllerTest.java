package com.example.springsecurity.controller;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.req.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
class ProductControllerTest {

    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE2MjY5ODQ1LCJleHAiOjE3MTYyNzM0NDV9.cTrr-H2_QmriKcZD_o3URYuRmEkScm3G1SJjs9EMNJA";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @WithMockUser(username = "huseyn", roles = {"ADMIN"})
    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testAddProductToCategory_AdminRole_Success(){

        long categoryId = 1L;
        String productName = "Test Product";
        String productDescription = "Test Description";
        BigDecimal productPrice = BigDecimal.valueOf(100.00);
        int productQuantity = 10;
        BigDecimal productDiscountPrice = BigDecimal.valueOf(90.00);
        BigDecimal productDiscount = BigDecimal.valueOf(10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", productName);
        body.add("description", productDescription);
        body.add("price", productPrice);
        body.add("quantity", productQuantity);
        body.add("discountPrice", productDiscountPrice);
        body.add("discount", productDiscount);
        body.add("image", new FileSystemResource("build/resources/main/static/6348.jpg"));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/products/" + categoryId),
                HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product added to category successfully", response.getBody());
    }


    @Test
    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAllProducts(){
        int page = 1;
        int pageSize = 2;

        String url = "http://localhost:" + port + "/products?page=" + page + "&pageSize=" + pageSize;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        HttpEntity<Void> httpEntity=new HttpEntity<>(headers);

        ResponseEntity<RestResponsePage<ProductDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<ProductDto> products = response.getBody();
        assertNotNull(products);
        assertEquals(pageSize, products.getSize());

    }

    @Test
    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "huseyn", roles = {"ADMIN"})
    public void testChangeProductImages()  {
        long productId = 1L;
        String url = createURLWithPort("/products/" + productId + "/images") ;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", token);

        Resource image = new ClassPathResource("static/6348.jpg");
        //предоставляет структуру данных, позволяющую хранить несколько значений для одного ключа.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                requestEntity,
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the image has been changed on the server
        assertTrue(Files.exists(Paths.get("build/resources/main/static/6348.jpg")));
    }


    @Test
    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetProductById() {
        Long productId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        String url = createURLWithPort("/products/" + productId) ;

        HttpEntity<Void> httpEntity=new HttpEntity<>(headers);
        ResponseEntity<ProductDto> response = restTemplate.exchange(url,
                HttpMethod.GET,
                httpEntity,
                ProductDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ProductDto productDto = response.getBody();

        // Verify product details
        assertEquals(productId, productDto.getId());
        assertEquals("samsung", productDto.getName());
        assertEquals("description", productDto.getDescription());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(productDto.getPrice()));
        assertNull(productDto.getDiscountPrice());
        assertEquals(10, productDto.getQuantity());
        assertEquals("/static/6348.jpg", productDto.getImageUrl());
        assertEquals("phones", productDto.getCategoryName());
        assertFalse(productDto.isDeleted());
    }

    @Test
    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "huseyn", roles = {"ADMIN"})
    public void testUpdateProduct_AdminRole_Success() {

        long productId = 1L;
        ProductRequest updatedProductRequest = new ProductRequest();
        updatedProductRequest.setName("Updated Product");
        updatedProductRequest.setDescription("Updated Description");
        updatedProductRequest.setPrice(BigDecimal.valueOf(200.00));
        updatedProductRequest.setUpdatedAt(LocalDateTime.now());
        updatedProductRequest.setQuantity(20);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<ProductRequest> requestEntity = new HttpEntity<>(updatedProductRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/products/" + productId),
                HttpMethod.PUT, requestEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated success", response.getBody());;
    }


    @Test
    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "huseyn", roles = {"ADMIN"})
    public void testDeleteProduct() {
        long productId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<ProductRequest> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort("/products/" + productId),
                HttpMethod.DELETE, requestEntity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }



    @Test

    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "huseyn", roles = {"ADMIN"})
    public void testSearchProductByName() {
        String keyword = "samsung";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>( headers);

        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                createURLWithPort("/products/search?keyword=" + keyword),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProductDto> products = response.getBody();

        assert products != null;
        assertEquals(1, products.size());

    }








    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}