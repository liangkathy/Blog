package com.example.blog.repository;

import com.example.blog.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITagRepository extends JpaRepository<Tag, Integer> {
    public Tag findByName(String name);
}
