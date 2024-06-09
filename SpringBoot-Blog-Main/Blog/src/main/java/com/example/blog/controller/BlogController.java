package com.example.blog.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.blog.dto.BlogDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Comment;
import com.example.blog.model.Tag;
import com.example.blog.service.BlogService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blogs")
@CrossOrigin(origins = "*")
public class BlogController {
    @Autowired
    BlogService blogService;

    //GET
    //get all blogs
    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    //get blog by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(blogService.getBlogById(id)); //returns blog to postman
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id not found
        }
    }

    //POST
    //create blog
    @PostMapping
    public ResponseEntity<?> createBlog(@Valid @RequestBody BlogDTO blogDTO) {
        try {
            return new ResponseEntity<>(blogService.createBlog(blogDTO), HttpStatus.CREATED);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //handle error thrown when blog is null
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id, user id, or tag ids (if given) not found
        }
    }


    //PUT
    //update blog
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable Integer id, @Valid @RequestBody Blog blog) {
        try {
            return ResponseEntity.ok(blogService.updateBlog(id, blog));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //handle exception if blog body is null
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //handle exception if blog id not found
        }
    }

    //DELETE
    //delete blog
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Integer id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.noContent().build(); //nothing to return after delete
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id not found
        }
    }


    //additional functionalities
    //add comment to blog
    @PutMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Integer id, @Valid @RequestBody Comment comment) {
        try {
            return ResponseEntity.ok(blogService.addCommentToBlog(id, comment));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if given null comment body
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id or user id not found
        }
    }

    //get blog by keyword (~search function)
    @GetMapping(params = "keyword")
    public ResponseEntity<?> getBlogsByKeyword(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(blogService.getBlogsByKeyword(keyword));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //for illegal argument exception, blank/empty keyword param
        }
    }

    //add new tag to blog
    @PutMapping("/{id}/tags")
    public ResponseEntity<?> addTagToBlog(@PathVariable Integer id, @Valid @RequestBody Tag tag) {
        try {
            return ResponseEntity.ok(blogService.addTagToBlog(id, tag));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); //if tag name exists
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if tag body is null
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id not found
        }
    }

    //remove tag from blog
    @PutMapping("/{id}/tags/{tagId}")
    public ResponseEntity<?> removeTagFromBlog(@PathVariable Integer id, @PathVariable Integer tagId) {
        try {
            return ResponseEntity.ok(blogService.removeTagFromBlog(id, tagId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id or tag id not found
        }
    }


}
