package com.example.springsecurity.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class FutureOrPresentValidator implements ConstraintValidator<FutureOrPresentLocalDateTime, String> {

    @Override
    public void initialize(FutureOrPresentLocalDateTime constraintAnnotation) {
    }


    //delayem parse String v localdatetime v formate mm/yy
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Разрешаем пустые даты
        }
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiration = YearMonth.parse(value, formatter);
        LocalDate expirationDate = expiration.atEndOfMonth();
        return !expirationDate.isBefore(currentDate.toLocalDate());
    }
}
