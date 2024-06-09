package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tag")
    @SequenceGenerator(name = "seq_tag", allocationSize = 1)
    private Integer id;

    @NotBlank(message = "Tag name cannot be blank or null")
    private String name;

    @NotBlank(message = "Tag description cannot be blank or null")
    private String description;

    //many-to-many relationship with blog
    @JsonIgnore
    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.MERGE, CascadeType.PERSIST}) //when tag is added/updated > do the same to blog
    private List<Blog> blogs = new ArrayList<>();
}
