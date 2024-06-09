package com.example.blog.repository;

import com.example.blog.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBlogRepository extends JpaRepository<Blog, Integer> {
    @Query("SELECT b FROM Blog b WHERE " +
            "lower(b.title) LIKE lower(CONCAT('%',:keyword,'%')) OR " +
            "lower(b.content) LIKE lower(CONCAT('%',:keyword,'%'))")
    List<Blog> findBlogByKeywordInTitleOrContent(@Param("keyword") String keyword);

    //query translation: find all blogs from Blog where the title or content contain the keyword
    // (using like and % - anything before or after keyword)
}
