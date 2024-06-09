package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_comment")
    @SequenceGenerator(name = "seq_comment", allocationSize = 1)
    private Integer id;

    @NotBlank(message = "Comment text cannot be blank or null")
    private String text;

    private Integer likes;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @CreationTimestamp //saves time entity saved
    @Column(name = "commented_at")
    private LocalDateTime timestamp;

    @NotBlank(message = "Commenter username cannot be blank or null")
    @Column(name = "commented_by")
    private String commenterUsername;

    //many-to-one relationship with blog
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    @ToString.Exclude //need to exclude blog from Lombok's @Data toString method prevent infinite recursion > StackOverflow
    private Blog blog;

}
