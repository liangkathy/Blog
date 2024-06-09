package com.example.blog.controller;

import com.example.blog.dto.CommentDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import com.example.blog.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

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


    ObjectMapper mapper = new ObjectMapper();

    //method to convert object to JSON string
    public String convertToJSON(Object object) throws Exception {
        try {
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(object);
            return json;
        } catch (Exception e) {
            throw new Exception(e.getLocalizedMessage());
        }
    }


    //---GET ALL COMMENTS---
    //HAPPY PATH
    @Test
    public void testGetAllComments() throws Exception {
        List<Comment> mockComments = Arrays.asList(mockComment, mockComment2);

        when(commentService.getAllComments()).thenReturn(mockComments);
        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockComments)));

        verify(commentService, times(1)).getAllComments();
    }


    //---GET COMMENT BY ID---
    //HAPPY PATH
    @Test
    public void testGetCommentById() throws Exception {
        System.out.println(LocalDateTime.now());
        String jsonComment = convertToJSON(mockComment);

        when(commentService.getCommentById(1)).thenReturn(mockComment);
        mockMvc.perform(get("/comments/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonComment));

        verify(commentService, times(1)).getCommentById(1);
    }

    //SAD PATH - id doesn't exist
    @Test
    public void testGetCommentsByIdFail() throws Exception {
        when(commentService.getCommentById(10000)).thenThrow(new Exception("Comment not found"));
        mockMvc.perform(get("/comments/{id}",10000))
                .andExpect(status().isNotFound());
    }



    //---ADD COMMENT---
    //HAPPY PATH
    @Test
    public void testCreateCommentPass() throws Exception {
        when(commentService.createComment(any(CommentDTO.class))).thenReturn(mockComment);
        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockCommentDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(convertToJSON(mockComment)));

        verify(commentService, times(1)).createComment(any(CommentDTO.class));
    }

    //SAD PATH - null comment
    @Test
    public void testCreateCommentNull() throws Exception {
        when(commentService.createComment(null)).thenThrow(new NullPointerException("Comment cannot be null"));
        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    //SAD PATH - blog id not found
    @Test
    public void testCreateCommentBlogFail() throws Exception {
        mockCommentDTO.setBlogId(10000); //giving the comment DTO a blog that doesn't exist

        when(commentService.createComment(mockCommentDTO)).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockCommentDTO)))
                .andExpect(status().isNotFound());
    }

    //SAD PATH - username doesn't exist
    @Test
    public void testCreateCommentUserFail() throws Exception {
        mockCommentDTO.setCommenterUsername("TestUsername"); //giving nonexistent username

        when(commentService.createComment(mockCommentDTO)).thenThrow(new Exception("Commenter username does not exist"));
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockCommentDTO)))
                .andExpect(status().isNotFound());
    }

    //---UPDATE COMMENT---
    //HAPPY PATH
    @Test
    public void testUpdateComment() throws Exception {
        mockComment.setId(mockComment2.getId()); //for testing only
        mockComment.setTimestamp(mockComment2.getTimestamp()); //for testing only

        mockComment.setText(mockComment2.getText());
        mockComment.setLikes(mockComment2.getLikes());
        mockComment.setCommenterUsername(mockComment2.getCommenterUsername());

        String jsonComment2 = convertToJSON(mockComment2);

        when(commentService.updateComment(anyInt(), any(Comment.class))).thenReturn(mockComment);
        mockMvc.perform(put("/comments/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonComment2))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonComment2));

        verify(commentService, times(1)).updateComment(anyInt(), any(Comment.class));
    }

    //SAD PATH - comment not found
    @Test
    public void testUpdateCommentFail() throws Exception {
        when(commentService.updateComment(anyInt(), any(Comment.class))).thenThrow(new Exception("Comment not found"));
        mockMvc.perform(put("/comments/{id}",10000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockComment2)))
                .andExpect(status().isNotFound());
    }

    //SAD PATH - comment null
    @Test
    public void testUpdateCommentNull() throws Exception {
        mockMvc.perform(put("/comments/{id}",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }



    //---DELETE COMMENT---
    //HAPPY PATH
    @Test
    public void testDeleteCommentPass() throws Exception {
        mockMvc.perform(delete("/comments/{id}",1))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1);
    }

    //SAD PATH
    @Test
    public void testDeleteCommentFail() throws Exception {
        doThrow(new Exception("Comment not found")).when(commentService).deleteComment(10000); //comment id doesnt exist
        mockMvc.perform(delete("/comments/{id}",10000))
                .andExpect(status().isNotFound());
    }



}
