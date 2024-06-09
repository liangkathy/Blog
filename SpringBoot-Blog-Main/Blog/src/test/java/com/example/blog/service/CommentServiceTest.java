package com.example.blog.service;

import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.NotificationDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.ICommentRepository;
import com.example.blog.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentServiceTest {
    @Autowired
    CommentService commentService;

    @MockBean
    ICommentRepository commentRepository;

    @MockBean
    IBlogRepository blogRepository;

    @MockBean
    IUserRepository userRepository;

    @MockBean
    RestTemplate restTemplate;

    private Comment createComment(Integer id, String text, Integer likes, String commenterUsername){
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setLikes(likes);
        comment.setTimestamp(LocalDateTime.now());
        comment.setCommenterUsername(commenterUsername);
        Blog blog = new Blog();
        comment.setBlog(blog);
        return comment;
    }

    private CommentDTO createCommentDTO(String text, Integer likes, String commenterUsername, Integer blogId){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText(text);
        commentDTO.setLikes(likes);
        commentDTO.setCommenterUsername(commenterUsername);
        commentDTO.setBlogId(blogId);

        return commentDTO;
    }

    private Comment mockComment = createComment(1, "Text", 25, "User123");
    private Comment mockComment2 = createComment(2, "Text2", 50, "User321");

    private CommentDTO mockCommentDTO = createCommentDTO("Text", 25, "User123", 1);

    private Blog mockBlog = new Blog();
    private User mockUser = new User();


    //---GET ALL COMMENTS---
    //HAPPY PATH
    @Test
    public void testGetAllCommentsPass(){
        List<Comment> mockComments = Arrays.asList(mockComment, mockComment2);

        when(commentRepository.findAll()).thenReturn(mockComments);
        List<Comment> resultComments = commentService.getAllComments();

        assertEquals(mockComments, resultComments, "The result list and mock list should match");
        assertEquals(2, resultComments.size(), "The result list size should be 2");

        verify(commentRepository, times(1)).findAll();
    }

    //SAD PATH
    @Test
    public void testGetAllCommentsFail(){
        List<Comment> mockComments = Arrays.asList(mockComment, mockComment2);

        when(commentRepository.findAll()).thenReturn(mockComments);
        List<Comment> resultComments = commentService.getAllComments();

        assertNotEquals(3, resultComments.size(), "The result list size should not be 3");
    }

    //---GET COMMENT BY ID---
    //HAPPY PATH
    @Test
    public void testGetCommentByIdPass() throws Exception {
        when(commentRepository.findById(1)).thenReturn(Optional.of(mockComment));
        Comment result = commentService.getCommentById(1);

        assertEquals(mockComment, result, "The result comment and mock comment should match");

        verify(commentRepository, times(1)).findById(1);
    }

    //SAD PATH
    @Test
    public void testGetCommentByIdFail() throws Exception {
        assertThrows(Exception.class, () -> commentService.getCommentById(10000)); //id doesn't exist
    }

    //---ADD COMMENT---
    //HAPPY PATH
    @Test
    public void testCreateCommentPass() throws Exception {
        List<Comment> mockComments = new ArrayList<>();
        mockBlog.setUser(mockUser);
        mockBlog.setComments(mockComments); //need list of comments in blog to be able to get comments in service method

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog)); //confirms blog exists for comment to be added
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser); //username must exist for comment to be created
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment); //mock save and return
        Comment result = commentService.createComment(mockCommentDTO);

        assertEquals(mockComment, result, "The result comment and mock comment should match");

        verify(blogRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }


    //SAD PATH - null comment
    @Test
    public void testCreateCommentNull() throws Exception {
        assertThrows(NullPointerException.class, () -> commentService.createComment(null)); //null comment
    }

    //SAD PATH - no blog found
    @Test
    public void testCreateCommentBlogFail() throws Exception {
        when(blogRepository.findById(anyInt())).thenReturn(null); //no blog found
        assertThrows(Exception.class, () -> commentService.createComment(mockCommentDTO));
    }

    //SAD PATH - username doesn't exist
    @Test
    public void testCreateCommentUsernameFail() throws Exception {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog)); //confirms blog exists to get to username check
        when(userRepository.findByUsername(anyString())).thenReturn(null); //no matching user found

        assertThrows(Exception.class, () -> commentService.createComment(mockCommentDTO));
    }


    //---UPDATE COMMENT---
    //HAPPY PATH
    @Test
    public void testUpdateCommentPass() throws Exception {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);
        Comment updatedComment = commentService.updateComment(1, mockComment2);

        assertEquals(mockComment, updatedComment, "The result comment and mock comment should match");

        verify(commentRepository, times(1)).findById(anyInt());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    //SAD PATH - comment id not found
    @Test
    public void testUpdateCommentFail() throws Exception {
        assertThrows(Exception.class, () -> commentService.updateComment(10000, mockComment2)); //id doesn't exist
    }

    //SAD PATH - comment body null
    @Test
    public void testUpdateCommentNull() throws Exception {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(mockComment));
        assertThrows(NullPointerException.class, () -> commentService.updateComment(1, null)); //null comment
    }


    //---DELETE COMMENT---
    //HAPPY PATH
    @Test
    public void testDeleteCommentPass() throws Exception {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(mockComment));
        commentService.deleteComment(1);

        verify(commentRepository, times(1)).findById(anyInt());
        verify(commentRepository, times(1)).delete(mockComment);
    }

    //SAD PATH
    @Test
    public void testDeleteCommentFail() throws Exception {
        assertThrows(Exception.class, () -> commentService.deleteComment(10000)); //id does not exist
    }

    //---CREATE NOTIFICATION---
    //SAD PATH - happy already mostly tested with other methods
    @Test
    public void testCreateNotificationFail() throws Exception {
        NotificationDTO notificationDTO = new NotificationDTO(); //null DTO, will throw error on microservice side
        ResponseEntity<RestTemplate> response = new ResponseEntity<>(restTemplate, HttpStatus.BAD_REQUEST);
        
        when(restTemplate.postForEntity("http://localhost:8081/notifications", notificationDTO, RestTemplate.class)).thenReturn(response);
        assertThrows(Exception.class, ()-> commentService.createNotification(mockComment,mockBlog));
    }


}
