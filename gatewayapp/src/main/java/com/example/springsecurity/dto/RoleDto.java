package com.example.springsecurity.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;
@Data
public class RoleDto {

    private Long id;

    private String role;

   Set<UserDto> userDtos=new HashSet<>();
}
