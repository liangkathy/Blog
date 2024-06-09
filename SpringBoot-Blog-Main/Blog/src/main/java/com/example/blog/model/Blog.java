package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_blog")
    @SequenceGenerator(name = "seq_blog", allocationSize = 1)
    private Integer id;

    @NotBlank(message = "Blog title cannot be null or blank")
    private String title;

    @NotBlank(message = "Blog content cannot be null or blank")
    private String content;

    private Integer likes;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @CreationTimestamp //saves time entity saved
    @Column(name = "created_at")
    private LocalDateTime creationDate;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @UpdateTimestamp //saves update time stamp (such as put request)
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedDate;

    //many-to-one relationship with user
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) //foreign key cannot be null, blog must have a posting user
    private User user;

    //one-to-many relationship with comment
    @OneToMany(mappedBy = "blog", cascade = CascadeType.REMOVE) //deletes comments when blog is deleted
    private List<Comment> comments;

    //many-to-many relationship with tag (join table code here)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}) //when blog is updated > update the field in tag
    @JoinTable(
            name = "blog_tag",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;


}
