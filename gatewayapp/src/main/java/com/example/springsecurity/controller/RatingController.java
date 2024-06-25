package com.example.springsecurity.controller;

import com.example.springsecurity.dto.RatingDto;
import com.example.springsecurity.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("ratings")
public class RatingController {
    private final RatingService ratingService;




    @Secured("USER")
    @PostMapping("/{id}/rate")
    public ResponseEntity<Map<String, String>> addRating(@PathVariable Long id, @RequestBody RatingDto ratingDto) {
        ratingDto.setProductId(id);  // Set productId in RatingDto
        ratingService.addRatingToProduct(ratingDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Thank you for your rating!");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }




}
