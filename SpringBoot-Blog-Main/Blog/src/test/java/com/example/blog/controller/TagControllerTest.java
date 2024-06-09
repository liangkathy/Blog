package com.example.blog.controller;

import com.example.blog.dto.TagDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Tag;
import com.example.blog.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
public class TagControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    TagService tagService;

    private List<Tag> createMockTags(Integer id, String name, String description){
        List<Tag> tags = new ArrayList<>();
        List<Blog> blogs = new ArrayList<>();
        for (int i = 0; i <= 3; i++) { //create three tag objects to add to list
            Tag tag = new Tag();
            tag.setId(id + i);
            tag.setName(name + i);
            tag.setDescription(description);
            tag.setBlogs(blogs);
            tags.add(tag);
        }
        return tags;
    }

    private List<TagDTO> createMockTagDTO(String name, String description){
        List<TagDTO> tagDTOs = new ArrayList<>();
        List<Integer> blogIds = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            TagDTO tagDTO = new TagDTO();
            tagDTO.setName(name + i);
            tagDTO.setDescription(description);
            tagDTO.setBlogIds(blogIds);
        }
        return tagDTOs;
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

    private List<Tag> mockTagsList = createMockTags(1, "Tag", "Description");
    private List<TagDTO> mockTagDTOList = createMockTagDTO("Tag", "Description");

    private Tag mockTag = createMockTag(1, "test", "test");
    private Tag mockTag2 = createMockTag(2, "test2", "test2");

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

    //---GET ALL TAGS---
    //HAPPY PATH
    @Test
    public void testGetAllTagsPass() throws Exception {
        List<Tag> mockTags = Arrays.asList(mockTag, mockTag2);

        String jsonTags = convertToJSON(mockTags);

        when(tagService.getAllTags()).thenReturn(mockTags);
        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonTags));

        verify(tagService, times(1)).getAllTags();
    }


    //---GET TAG BY ID---
    //HAPPY PATH
    @Test
    public void testGetTagByIdPass() throws Exception {
        when(tagService.getTagById(1)).thenReturn(mockTag);
        mockMvc.perform(get("/tags/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockTag)));
    }

    //SAD PATH - tag id not found
    @Test
    public void testGetTagByIdFail() throws Exception {
        when(tagService.getTagById(1)).thenThrow(new Exception("Tag not found"));
        mockMvc.perform(get("/tags/{id}", 1))
                .andExpect(status().isNotFound());
    }


    //---ADD TAG---
    //HAPPY PATH
    @Test
    public void testCreateTagPass() throws Exception {
        List<String> jsonTagsList = new ArrayList<>();
        for (Tag tag : mockTagsList) { //expected list should be 4 tags to match return
            convertToJSON(tag);
            jsonTagsList.add(convertToJSON(tag));
        }

        when(tagService.addTags(mockTagDTOList)).thenReturn(mockTagsList);
        mockMvc.perform(post("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockTagDTOList)))
                .andExpect(status().isCreated())
                .andExpect(content().json(String.valueOf(jsonTagsList)));

        verify(tagService,times(1)).addTags(mockTagDTOList);
    }


    //SAD PATH - null tag body
    @Test
    public void testCreateTagNull() throws Exception {
        when(tagService.addTags(null)).thenThrow(new NullPointerException("Tag cannot be null"));
        mockMvc.perform(post("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    //SAD PATH - tag name exists
    @Test
    public void testCreateTagNameExists() throws Exception {
        when(tagService.addTags(anyList())).thenThrow(new DuplicateKeyException("Tag name already exists"));
        mockMvc.perform(post("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockTagDTOList)))
                .andExpect(status().isConflict());

        verify(tagService,times(1)).addTags(anyList());
    }

    //SAD PATH - blog not found when adding tags with blog id list
    @Test
    public void testCreateTagBlogNotFound() throws Exception {
        when(tagService.addTags(anyList())).thenThrow(new Exception("Blog not found"));
        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockTagDTOList)))
                .andExpect(status().isNotFound());

        verify(tagService,times(1)).addTags(anyList());
    }


    //---UPDATE TAG---
    //HAPPY PATH
    @Test
    public void testUpdateTagPass() throws Exception {
        mockTag.setId(mockTag.getId()); //for testing only

        mockTag.setName(mockTag.getName());
        mockTag.setDescription(mockTag.getDescription());
        mockTag.setBlogs(mockTag.getBlogs());

        when(tagService.updateTag(1, mockTag2)).thenReturn(mockTag);
        mockMvc.perform(put("/tags/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockTag2)))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockTag)));

        verify(tagService,times(1)).updateTag(1, mockTag2);
    }

    //SAD PATH - tag id not found
    @Test
    public void testUpdateTagFail() throws Exception {
        when(tagService.updateTag(10000, mockTag2)).thenThrow(new Exception("Tag not found"));
        mockMvc.perform(put("/tags/{id}", 10000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockTag2)))
                .andExpect(status().isNotFound());
    }

    //SAD PATH - null tag body
    @Test
    public void testUpdateTagNull() throws Exception {
        when(tagService.updateTag(1, null)).thenThrow(new NullPointerException("Tag cannot be null"));
        mockMvc.perform(put("/tags/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    //SAD PATH - tag name exists
    @Test
    public void testUpdateTagNameExists() throws Exception {
        when(tagService.updateTag(1, mockTag2)).thenThrow(new DuplicateKeyException("Tag name already exists"));
        mockMvc.perform(put("/tags/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockTag2)))
                .andExpect(status().isConflict());
    }


    //---DELETE TAG---
    //HAPPY PATH
    @Test
    public void testDeleteTagPass() throws Exception {
        mockMvc.perform(delete("/tags/{id}", 1))
                .andExpect(status().isNoContent());

        verify(tagService,times(1)).deleteTag(1);
    }

    //SAD PATH - tag id not found
    @Test
    public void testDeleteTagFail() throws Exception {
        doThrow(new Exception("Tag not found")).when(tagService).deleteTag(10000);
        mockMvc.perform(delete("/tags/{id}", 10000))
                .andExpect(status().isNotFound());
    }


    //---GET TAG BY NAME---
    //HAPPY PATH
    @Test
    public  void testGetTagByNamePass() throws Exception {
        when(tagService.getTagByName("test")).thenReturn(mockTag);
        mockMvc.perform(get("/tags")
                .param("name","test"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockTag)));

        verify(tagService,times(1)).getTagByName("test");
    }


    //---GET BLOGS BY TAG ID---
    //HAPPY PATH
    @Test
    public void testGetBlogsByTagPass() throws Exception {
        List<Blog> mockBlogs = new ArrayList<>();
        when(tagService.getBlogsByTag(anyInt())).thenReturn(mockBlogs);
        mockMvc.perform(get("/tags/{id}/blogs", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlogs)));

        verify(tagService,times(1)).getBlogsByTag(anyInt());
    }

    //SAD PATH - tag id not found
    @Test
    public void testGetBlogsByTagFail() throws Exception {
        when(tagService.getBlogsByTag(10000)).thenThrow(new Exception("Tag not found"));
        mockMvc.perform(get("/tags/{id}/blogs", 10000))
                .andExpect(status().isNotFound());

        verify(tagService,times(1)).getBlogsByTag(10000);
    }





}
