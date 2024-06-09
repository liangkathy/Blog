package com.example.blog.controller;

import com.example.blog.dto.CommentDTO;
import com.example.blog.model.Comment;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    @Autowired
    CommentService commentService;

    //GET
    //get all comments
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments(){
        return ResponseEntity.ok(commentService.getAllComments());
    }

    //get comment by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(commentService.getCommentById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if comment id not found
        }
    }


    //POST
    //add comment
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentDTO commentDTO){
        try {
            return new ResponseEntity<>(commentService.createComment(commentDTO), HttpStatus.CREATED);
        } catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if comment body is null
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if blog id not found or commenter username doesn't exist
        }
    }


    //PUT
    //update comment
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Integer id, @Valid @RequestBody Comment comment){
        try {
            return ResponseEntity.ok(commentService.updateComment(id, comment));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if given null comment body
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if comment id not found
        }
    }


    //DELETE
    //delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@Valid @PathVariable Integer id){
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if comment id not found
        }
    }
}
