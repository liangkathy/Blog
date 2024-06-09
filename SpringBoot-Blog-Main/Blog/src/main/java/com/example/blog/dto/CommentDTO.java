package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @NotBlank(message = "Comment text cannot be blank or null")
    private String text;
    private Integer likes;
    @NotBlank(message = "Commenter username cannot be blank or null")
    private String commenterUsername;
    @NotNull(message = "Blog ID cannot be null")
    private Integer blogId;

}
