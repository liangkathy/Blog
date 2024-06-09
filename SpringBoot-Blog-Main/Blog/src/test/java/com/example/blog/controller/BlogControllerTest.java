package com.example.blog.controller;

import com.example.blog.dto.BlogDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Comment;
import com.example.blog.model.Tag;
import com.example.blog.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BlogController.class)
public class BlogControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BlogService blogService;

    private Blog createBlog(Integer id, String title, String content, Integer likes) {
        Blog blog = new Blog();

        blog.setId(id);
        blog.setTitle(title);
        blog.setContent(content);
        blog.setLikes(likes);
        blog.setCreationDate(LocalDateTime.now());
        blog.setLastModifiedDate(LocalDateTime.now());

        List<Comment> mockComments = new ArrayList<>();
        mockComments.add(new Comment());
        blog.setComments(mockComments);

        return blog;
    }

    private BlogDTO createBlogDTO(String title, String content, Integer likes, Integer userId) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setTitle(title);
        blogDTO.setContent(content);
        blogDTO.setLikes(likes);
        blogDTO.setUserId(userId);

        List<Integer> tagIds = Arrays.asList(1, 2);
        blogDTO.setTagIds(tagIds);

        return blogDTO;
    }

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

    private Tag createMockTag(Integer id, String name, String description){
        Tag tag = new Tag();
        List<Blog> blogs = new ArrayList<>();
        tag.setId(id);
        tag.setName(name);
        tag.setDescription(description);
        tag.setBlogs(blogs);
        return tag;
    }


    private Blog mockBlog = createBlog(1, "Blog Title", "Blog Content", 100);
    private Blog mockBlog2 = createBlog(2, "Blog Title 2", "Blog Content 2", 45);

    private BlogDTO mockBlogDTO = createBlogDTO("Blog Title", "Blog Content", 100, 1);

    private Tag mockTag = createMockTag(1, "Tag Title", "Tag Description");
    private Comment mockComment = createComment(1, "Text", 25, "TestUsername");

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

    //---GET ALL BLOGS---
    //HAPPY PATH
    @Test
    public void testGetAllBlogsPass() throws Exception {
        List<Blog> mockBlogs = Arrays.asList(mockBlog, mockBlog2);
        when(blogService.getAllBlogs()).thenReturn(mockBlogs);
        mockMvc.perform(get("/blogs"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlogs)));

        verify(blogService, times(1)).getAllBlogs();
    }


    //---GET BLOG BY ID---
    //HAPPY PATH
    @Test
    public void testGetBlogByIdPass() throws Exception {
        when(blogService.getBlogById(1)).thenReturn(mockBlog);
        mockMvc.perform(get("/blogs/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlog)));

        verify(blogService, times(1)).getBlogById(1);
    }

    //SAD PATH
    @Test
    public void testGetBlogByIdFail() throws Exception {
        when(blogService.getBlogById(10000)).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(get("/blogs/{id}", 10000))
                .andExpect(status().isNotFound());
    }


    //---ADD BLOG---
    //HAPPY PATH
    @Test
    public void testCreateBlogPass() throws Exception {
        when(blogService.createBlog(mockBlogDTO)).thenReturn(mockBlog);
        mockMvc.perform(post("/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockBlogDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(convertToJSON(mockBlog)));

        verify(blogService, times(1)).createBlog(mockBlogDTO);
    }

    //SAD PATH - blog null
    @Test
    public void testCreateBlogFail() throws Exception {
        when(blogService.createBlog(null)).thenThrow(new NullPointerException("Blog cannot be null"));
        mockMvc.perform(post("/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    //SAD PATH - user not found
    @Test
    public void testCreateBlogUserFail() throws Exception {
        when(blogService.createBlog(mockBlogDTO)).thenThrow(new Exception("User not found"));
        mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockBlogDTO)))
                .andExpect(status().isNotFound());

        verify(blogService, times(1)).createBlog(mockBlogDTO);
    }


    //---UPDATE BLOG---
    //HAPPY PATH
    @Test
    public void testUpdateBlogPass() throws Exception {
        mockBlog.setId(mockBlog2.getId()); //for testing only
        mockBlog.setCreationDate(mockBlog2.getCreationDate()); //for testing only
        mockBlog.setLastModifiedDate(mockBlog2.getLastModifiedDate()); //for testing only

        mockBlog.setTitle(mockBlog2.getTitle());
        mockBlog.setContent(mockBlog2.getContent());
        mockBlog.setLikes(mockBlog2.getLikes());

        when(blogService.updateBlog(1, mockBlog2)).thenReturn(mockBlog);
        mockMvc.perform(put("/blogs/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockBlog2)))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlog)));

        verify(blogService, times(1)).updateBlog(1, mockBlog2);
    }

    //SAD PATH - blog id not found
    @Test
    public void testUpdateBlogFail() throws Exception {
        when(blogService.updateBlog(10000, mockBlog2)).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(put("/blogs/{id}", 10000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockBlog2)))
                .andExpect(status().isNotFound());
    }

    //SAD PATH - blog null
    @Test
    public void testUpdateBlogNull() throws Exception {
        when(blogService.updateBlog(1, null)).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(put("/blogs/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }


    //---DELETE BLOG---
    //HAPPY PATH
    @Test
    public void testDeleteBlogPass() throws Exception {
        mockMvc.perform(delete("/blogs/{id}", 1))
                .andExpect(status().isNoContent());

        verify(blogService, times(1)).deleteBlog(1);
    }

    //SAD PATH
    @Test
    public void testDeleteBlogFail() throws Exception {
        doThrow(new Exception("Blog not found")).when(blogService).deleteBlog(10000);
        mockMvc.perform(delete("/blogs/{id}", 10000))
                .andExpect(status().isNotFound());
    }


    //---ADD COMMENT TO BLOG---
    //HAPPY PATH
    @Test
    public void testAddCommentToBlogPass() throws Exception {
        when(blogService.addCommentToBlog(anyInt(),any(Comment.class))).thenReturn(mockBlog);
        mockMvc.perform(put("/blogs/{id}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockComment)))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlog)));

        verify(blogService, times(1)).addCommentToBlog(anyInt(),any(Comment.class));
    }


    //SAD PATH - blog id not found
    @Test
    public void testAddCommentToBlogFail() throws Exception {
        when(blogService.addCommentToBlog(anyInt(),any(Comment.class))).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(put("/blogs/{id}/comments", 10000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockComment)))
                .andExpect(status().isNotFound());
    }

    //SAD PATH - comment null
    @Test
    public void testAddCommentToBlogNull() throws Exception {
        when(blogService.addCommentToBlog(1,null)).thenThrow(new NullPointerException("Comment cannot be null"));
        mockMvc.perform(put("/blogs/{id}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }


    //---GET BLOG BY KEYWORD---
    //HAPPY PATH
    @Test
    public void testGetBlogsByKeywordPass() throws Exception {
        List<Blog> mockBlogs = Arrays.asList(mockBlog, mockBlog2);
        when(blogService.getBlogsByKeyword(anyString())).thenReturn(mockBlogs);
        mockMvc.perform(get("/blogs")
                .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlogs)));

        verify(blogService, times(1)).getBlogsByKeyword("test");
    }


    //SAD PATH - keyword is blank
    @Test
    public void testGetBlogsByKeywordBlank() throws Exception {
        when(blogService.getBlogsByKeyword(anyString())).thenThrow(new NullPointerException("Keyword cannot be blank"));
        mockMvc.perform(get("/blogs")
                        .param("keyword", "   "))
                .andExpect(status().isBadRequest());
    }

    //---ADD NEW TAG TO BLOG---
    //HAPPY PATH
    @Test
    public void testAddTagToBlogPass() throws Exception {
        when(blogService.addTagToBlog(anyInt(),any(Tag.class))).thenReturn(mockBlog);
        mockMvc.perform(put("/blogs/{id}/tags", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockTag)))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlog)));

        verify(blogService, times(1)).addTagToBlog(anyInt(),any(Tag.class));
    }


    //SAD PATH - blog not found
    @Test
    public void testAddTagToBlogFail() throws Exception {
        when(blogService.addTagToBlog(anyInt(),any(Tag.class))).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(put("/blogs/{id}/tags", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockTag)))
                .andExpect(status().isNotFound());

        verify(blogService, times(1)).addTagToBlog(anyInt(),any(Tag.class));
    }

    //SAD PATH - tag name exists
    @Test
    public void testAddTagToBlogTagNameExists() throws Exception {
        when(blogService.addTagToBlog(anyInt(),any(Tag.class))).thenThrow(new DuplicateKeyException("Tag name already exists"));
        mockMvc.perform(put("/blogs/{id}/tags", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockTag)))
                .andExpect(status().isConflict());

        verify(blogService, times(1)).addTagToBlog(anyInt(),any(Tag.class));
    }

    //SAD PATH - tag body null
    @Test
    public void testAddTagToBlogTagNull() throws Exception {
        when(blogService.addTagToBlog(anyInt(),any(Tag.class))).thenThrow(new NullPointerException("Tag cannot be null"));
        mockMvc.perform(put("/blogs/{id}/tags", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockTag)))
                .andExpect(status().isBadRequest());

        verify(blogService, times(1)).addTagToBlog(anyInt(),any(Tag.class));
    }

    //---REMOVE TAG FROM BLOG---
//HAPPY PATH
    @Test
    public void testRemoveTagFromBlogPass() throws Exception {
        when(blogService.removeTagFromBlog(anyInt(), anyInt())).thenReturn(mockBlog);
        mockMvc.perform(put("/blogs/{id}/tags/{tagId}", 1, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlog)));

        verify(blogService, times(1)).removeTagFromBlog(anyInt(), anyInt());
    }

    //SAD PATH - blog not found
    @Test
    public void testRemoveTagFromBlogFail() throws Exception {
        when(blogService.removeTagFromBlog(anyInt(), anyInt())).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(put("/blogs/{id}/tags/{tagId}", 10000, 1))
                .andExpect(status().isNotFound());
    }


}
