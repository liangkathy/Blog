package com.example.blog.service;

import com.example.blog.dto.NotificationDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.User;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    @Autowired
    IUserRepository userRepository;

    @Autowired
    IBlogRepository blogRepository;

    @Autowired
    private RestTemplate restTemplate;

    //get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //get user by id
    public User getUserById(Integer id) throws Exception {
        return userRepository.findById(id).orElseThrow(() -> new Exception("User with id " + id + " not found"));
    }


    //create new user
    public User createUser(User user) throws Exception {
        if (user != null) {
            String username = user.getUsername();
            User existingUser = userRepository.findByUsername(username); //check if username already exists

            if (existingUser != null) {
                throw new DuplicateKeyException("Username " + username + " already taken");
            }
            return userRepository.save(user);
        } else {
            throw new NullPointerException("User cannot be null");
        }
    }

    //update user
    public User updateUser(Integer id, User user) throws Exception {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new Exception("User with id " + id + " not found"));
        if (user != null) {
            String username = user.getUsername();
            User matchingUser = userRepository.findByUsername(username); //check if username exists

            if (matchingUser != null) {
                throw new DuplicateKeyException("Username " + username + " already taken"); //stops here if username already exists
            }

            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(user.getPassword());
            existingUser.setEmail(user.getEmail());
            existingUser.setAddress(user.getAddress());
            return userRepository.save(existingUser);
        } else {
            throw new NullPointerException("User cannot be null");
        }
    }

    //delete user
    public void deleteUser(Integer id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User with id " + id + " not found"));
        userRepository.delete(user);
    }


    //additional functionalities
    //add new blog to user
    public User addBlogToUser(Integer id, Blog blog) throws Exception {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new Exception("User with id " + id + " not found"));

        if (blog != null) {
            blog.setUser(existingUser);
            if(blog.getLikes() == null) { //set likes to 0 if null
                blog.setLikes(0);
            }
            existingUser.getBlogs().add(blog);
            blogRepository.save(blog);
            return userRepository.save(existingUser);
        } else {
            throw new NullPointerException("Blog cannot be null");
        }
    }

    //get all blogs by user id
    public List<Blog> getBlogsByUserId(Integer id) throws Exception {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new Exception("User with id " + id + " not found"));

        return existingUser.getBlogs();
    }

    //microservice
    //get notifications of specific userId (aka bloggerId)
    public List<NotificationDTO> getNotificationsByUserId(Integer id) throws Exception {
        userRepository.findById(id).orElseThrow(() -> new Exception("User with id " + id + " not found"));

        //call to microservice endpoint
        ResponseEntity<NotificationDTO[]> response = restTemplate.getForEntity(
                "http://localhost:8081/notifications/users/{id}",
                NotificationDTO[].class,
                id);

        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            NotificationDTO[] notificationDTOs = response.getBody();

            return Arrays.asList(notificationDTOs);
        } else {
            throw new Exception("Unable to retrieve notifications");
        }

    }

    
}
