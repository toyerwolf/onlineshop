package com.example.springsecurity.service;

import com.example.springsecurity.dto.RatingDto;
import com.example.springsecurity.entity.Rating;

import java.util.Optional;

public interface RatingService {
    void addRatingToProduct(RatingDto ratingDto);

}
