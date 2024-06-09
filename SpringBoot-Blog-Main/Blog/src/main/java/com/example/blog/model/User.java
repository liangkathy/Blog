package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UserDetails")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user")
    @SequenceGenerator(name = "seq_user", allocationSize = 1)
    private Integer id;

    @NotBlank(message = "Username cannot be blank or null")
    private String username;

    @Email(message = "Email must be formatted as an email address")
    @NotBlank(message = "Email cannot be blank or null")
    private String email;

    @NotBlank(message = "Password cannot be blank or null")
    private String password;

    @JsonFormat(pattern="yyyy-MM-dd")
    @CreationTimestamp //saves time entity saved
    private LocalDate registrationDate; //needs to be tested, goal is to get date as "yyyy-MM-dd" format

    //one-to-one relationship with address (unidirectional)
    @NotNull(message = "Address is required")
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    //one-to-many relationship with blog
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE) //deletes blogs when user deleted
    private List<Blog> blogs;
}