package com.example.blog.controller;

import com.example.blog.dto.TagDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Tag;
import com.example.blog.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@CrossOrigin(origins = "*")
public class TagController {
    @Autowired
    TagService tagService;

    //GET
    //get all tags
    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    //get tag by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getTagById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(tagService.getTagById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if tag id not found
        }
    }

    //POST
    //add tags (takes list of tags to add multiple at once)
    @PostMapping
    public ResponseEntity<?> addTagsToBlog(@Valid @RequestBody List<TagDTO> tagDTOs) throws Exception {
        try {
            return new ResponseEntity<>(tagService.addTags(tagDTOs), HttpStatus.CREATED);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if tag body is null
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); //if tag name already exists, cannot have duplicate tag names
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id not found (if any are given)
        }
    }

    //PUT
    //update tag
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(@PathVariable Integer id, @Valid @RequestBody Tag tag) {
        try {
            return ResponseEntity.ok(tagService.updateTag(id, tag));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if given tag body is null
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); //if tag name exists already
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if tag id not found
        }
    }

    //DELETE
    //delete tag
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Integer id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if tag id not found
        }
    }

    //additional functionalities
    //get tags by name
    @GetMapping(params = "name")
    public ResponseEntity<?> getTagsByName(@RequestParam String name) {
        try {
            return ResponseEntity.ok(tagService.getTagByName(name));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if name param is blank/empty
        }
    }

    //get blogs by tag id
    @GetMapping("/{id}/blogs")
    public ResponseEntity<?> getBlogsByTag(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(tagService.getBlogsByTag(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if tag id not found
        }
    }
}
