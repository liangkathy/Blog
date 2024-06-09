package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {
    @NotBlank(message = "Tag name cannot be blank or null")
    private String name;
    @NotBlank(message = "Tag description cannot be blank or null")
    private String description;
    private List<Integer> blogIds;
}
