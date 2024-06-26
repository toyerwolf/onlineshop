package com.example.springsecurity.controller;

import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.dto.CategoryDtoForClient;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.dto.ProductDtoContainer;
import com.example.springsecurity.req.CategoryReq;
import com.example.springsecurity.req.CustomerCardReq;
import com.example.springsecurity.security.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.lang.invoke.CallSite;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
//nujen dlya toqo chtobi pri zapuske vsex testov ne bilo konfliktov
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CategoryControllerTest {

    @LocalServerPort
    private int port;

    private String userToken;
    private String adminToken;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    @BeforeEach
    public void setUp() {

        adminToken = "Bearer " + jwtTestUtil.generateToken(1L, "ADMIN");


        userToken = "Bearer " + jwtTestUtil.generateToken(2L, "USER");
    }




    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql(scripts = {"classpath:sql/category-add.sql"})
    void testGetAllCategories() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<List<CategoryDto>> responseEntity = restTemplate.exchange(
                createURLWithPort("/categories"),
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<CategoryDto> categories = responseEntity.getBody();
        assertNotNull(categories);
        assertEquals(1, categories.size());
    }

    @Test
    @Sql(scripts = {"classpath:sql/category-add.sql"})
    void testGetCategoryById() {
        long categoryId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<CategoryDtoForClient> responseEntity = restTemplate.exchange(
                createURLWithPort("/categories/" + categoryId),
                HttpMethod.GET,
                httpEntity,
                CategoryDtoForClient.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        CategoryDtoForClient category = responseEntity.getBody();
        assertNotNull(category);

    }

    @Test
    @Sql(scripts = {"classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testCreateCategory() {
        CategoryReq categoryReq = new CategoryReq();
        categoryReq.setCategoryId(2L);
        categoryReq.setName("Test");
        categoryReq.setDescription("Testov");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);

        HttpEntity<CategoryReq> httpEntity = new HttpEntity<>(categoryReq, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                createURLWithPort("/categories"),
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Category created successfully", responseEntity.getBody());
    }


    @Test
    @Sql(scripts = {"classpath:sql/category-add.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdateCategory() {
        long categoryId = 1L;
        CategoryDto updatedCategory = new CategoryDto();
        updatedCategory.setName("Updated Category Name");
        updatedCategory.setDescription("Updated Category Description");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);

        HttpEntity<CategoryDto> request = new HttpEntity<>(updatedCategory, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                createURLWithPort("/categories/" + categoryId),
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Updated successfully", responseEntity.getBody());
        assertNotNull(responseEntity.getBody());
    }


    @Test
    @Sql(scripts = {"classpath:sql/category-add.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql",

    },executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testDeleteCategory() {
        long categoryId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", adminToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                createURLWithPort("/categories/" + categoryId),
                HttpMethod.DELETE,
                request,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
    @Test
    @Sql(scripts = {"classpath:sql/category-add.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql",
            "classpath:sql/productadd.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetProductsByCategoryId(){
        long categoryId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", userToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ProductDtoContainer> responseEntity = restTemplate.exchange(
                createURLWithPort("/categories/" + categoryId + "/products"),
                HttpMethod.GET,
                httpEntity,
                ProductDtoContainer.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ProductDtoContainer container = responseEntity.getBody();
        assertNotNull(container);
        List<ProductDto> products = container.getProducts();
        assertNotNull(products);
        assertEquals(2, products.size());
    }











    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}