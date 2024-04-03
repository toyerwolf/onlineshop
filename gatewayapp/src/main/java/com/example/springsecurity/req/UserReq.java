package com.example.springsecurity.req;

import com.example.springsecurity.dto.RoleDto;
import com.example.springsecurity.validation.Phone;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserReq {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @Phone
    private String phoneNumber;

    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 5, max = 8, message = "Username length must be between 5 and 8 characters")
    @Column(unique = true)
    private String username;


    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()-_=+]{8,12}$",message = "password is not required")
    private String password;

    @Email
    private String email;

    private Boolean locked;

    List<RoleDto> roleDtos=new ArrayList<>();

    private List<String> roles;
}
