package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_address")
    @SequenceGenerator(name = "seq_address", allocationSize = 1)
    private Integer id;

    @NotBlank(message = "Street cannot be blank or null")
    private String street;

    @NotBlank(message = "City cannot be blank or null")
    private String city;

    @NotBlank(message = "State cannot be blank or null")
    private String state;

    @NotBlank(message = "Zipcode cannot be blank or null")
    private String zipCode;

    @NotBlank(message = "Country cannot be blank or null")
    private String country;

    @JsonIgnore
    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL)
    @ToString.Exclude
    private User user;
}
