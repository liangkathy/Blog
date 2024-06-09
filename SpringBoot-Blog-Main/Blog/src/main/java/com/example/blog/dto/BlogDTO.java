package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDTO {
    @NotBlank(message = "Blog title cannot be blank or null")
    private String title;
    @NotBlank(message = "Blog content cannot be blank or null")
    private String content;
    private Integer likes;

    @NotNull(message = "User ID cannot be null")
    private Integer userId;
    private List<Integer> tagIds;

}
