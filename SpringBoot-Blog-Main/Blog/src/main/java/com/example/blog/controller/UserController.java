package com.example.blog.controller;

import com.example.blog.dto.NotificationDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.User;
import com.example.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserService userService;

    //GET
    //get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    //get user by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if user id not found
        }
    }

    //POST
    //create new user
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
        } catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if given null user body
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); //error 409 conflict best fits a username already exists
        }
    }

    //PUT
    //update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if given null user body
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); //if username already taken
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if user id not found
        }
    }

    //DELETE
    //delete user
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if user id not found
        }
    }


    //additional functionalities
    //add new blog to user
    @PutMapping("/{id}/blogs")
    public ResponseEntity<?> addBlogToUser(@PathVariable Integer id, @Valid @RequestBody Blog blog) {
        try {
            return ResponseEntity.ok(userService.addBlogToUser(id, blog));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if blog body is null
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if user id not found
        }
    }

    //get all blogs by user id
    @GetMapping("/{id}/blogs")
    public ResponseEntity<?> getBlogsByUser(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.getBlogsByUserId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if user id not found
        }
    }

    //microservice
    //get notifications of specific userId (aka bloggerId) ~ microservice
    @GetMapping("/{id}/notifications")
    public ResponseEntity<?> getNotificationsByUserId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.getNotificationsByUserId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if user id not found or notifications not found
        }
    }


}
