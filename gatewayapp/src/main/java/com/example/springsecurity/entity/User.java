package com.example.springsecurity.entity;

import com.example.springsecurity.validation.Phone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users_test")
@NamedEntityGraph(name ="users_authors",attributeNodes=@NamedAttributeNode("roles"))
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @Phone
    private String phoneNumber;

    @Column(unique = true)
    private String username;


    private String password;


    private String email;

    private Boolean locked;

    private LocalDateTime passwordCreation;

    private LocalDateTime passwordExpiration;

    private boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if (passwordCreation == null) {
            passwordCreation = LocalDateTime.now();
        }
        if (passwordExpiration == null) {
            passwordExpiration = passwordCreation.plusDays(90);
        }
    }




    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "userstest_rolestest",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void setRole(Role role){
        roles.add(role);
        role.getUsers().add(this);
    }

}