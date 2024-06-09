package com.example.blog.service;

import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.NotificationDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.ICommentRepository;
import com.example.blog.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    ICommentRepository commentRepository;

    @Autowired
    IBlogRepository blogRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private Comment mapToComment(CommentDTO commentDTO, Blog existingBlog) throws Exception {
        Comment comment = new Comment();
        comment.setBlog(existingBlog); //this was already found by id in main method
        comment.setCommenterUsername(commentDTO.getCommenterUsername());
        if(commentDTO.getLikes() == null) { //set likes to 0 if given null
            comment.setLikes(0);
        } else {
            comment.setLikes(commentDTO.getLikes());
        }
        comment.setText(commentDTO.getText());
        return comment;
    }

    //get all comments
    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }

    //get comment by id
    public Comment getCommentById(Integer id) throws Exception {
        return commentRepository.findById(id).orElseThrow(() -> new Exception("Comment with id " + id + " not found"));
    }

    //add comment
    public Comment createComment(CommentDTO commentDTO) throws Exception {
        if(commentDTO != null) {
            Integer blogId = commentDTO.getBlogId();
            Blog existingBlog = blogRepository.findById(blogId).orElseThrow(() -> new Exception("Blog with id " + blogId + " not found"));

            String username = commentDTO.getCommenterUsername();
            User existingUser = userRepository.findByUsername(username);
            if(existingUser != null) { //ensure commenter user exists as user

                Comment comment = mapToComment(commentDTO, existingBlog); //map dto to comment object in order to save
                existingBlog.getComments().add(comment);
                blogRepository.save(existingBlog);

                comment.setBlog(existingBlog);
                Comment savedComment = commentRepository.save(comment); //save before notification sent so commentID can be passed
                //sending notification to microservice
                createNotification(comment, existingBlog); //pass parameters to method that communicates with notification microservice

                return savedComment; //returning saved comment - needed to instantiate to pass testing
            } else {
                throw new Exception("Commenter username does not exist as user");
            }
        } else {
            throw new NullPointerException("Comment cannot be null");
        }
    }

    //method to send saved comment info to microservice
    public void createNotification(Comment comment, Blog existingBlog) throws Exception {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setCommenterUsername(comment.getCommenterUsername());
        notificationDTO.setBloggerId(existingBlog.getUser().getId());
        notificationDTO.setCommentId(comment.getId());
        notificationDTO.setBlogId(existingBlog.getId());
        try {
            restTemplate.postForEntity("http://localhost:8081/notifications", notificationDTO, RestTemplate.class); //call to microservice endpoint
        } catch (Exception e) {
            throw new Exception("Notification could not be sent to the server");
        }

    }


    //update comment
    public Comment updateComment(Integer id, Comment comment) throws Exception {
        Comment existingComment = commentRepository.findById(id).orElseThrow(() -> new Exception("Comment with id " + id + " not found"));
        if(comment != null) {
            existingComment.setText(comment.getText());
            if(comment.getLikes() == null) { //set likes to 0 if given null
                existingComment.setLikes(0);
            } else {
                existingComment.setLikes(comment.getLikes());
            }
            //intentionally not setting commenter username or blog because those should not have an edit/update feature
            return commentRepository.save(existingComment);
        } else {
            throw new NullPointerException("Comment cannot be null");
        }
    }

    //delete comment
    public void deleteComment(Integer id) throws Exception {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new Exception("Comment with id " + id + " not found"));
        commentRepository.delete(comment);
    }
}
