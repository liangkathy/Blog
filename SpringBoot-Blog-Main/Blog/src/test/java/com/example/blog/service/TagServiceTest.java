package com.example.blog.service;

import com.example.blog.dto.TagDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Tag;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.ITagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TagServiceTest {
    @Autowired
    TagService tagService;

    @MockBean
    ITagRepository tagRepository;

    @MockBean
    IBlogRepository blogRepository;

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
            tagDTO.getBlogIds().add(i+1);

            tagDTOs.add(tagDTO);
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


    //---GET ALL TAGS---
    //HAPPY PATH
    @Test
    public void testGetAllTagsPass(){
        List<Tag> mockTags = Arrays.asList(mockTag, mockTag2);
        when(tagRepository.findAll()).thenReturn(mockTags);
        List<Tag> resultTags = tagService.getAllTags();

        assertEquals(mockTags, resultTags, "The result list should match the mock list");
        assertEquals(2, resultTags.size(), "The result list size should be 2");

        verify(tagRepository, times(1)).findAll();
    }


    //SAD PATH
    @Test
    public void testGetAllTagsFail(){
        List<Tag> mockTags = Arrays.asList(mockTag, mockTag2);

        when(tagRepository.findAll()).thenReturn(mockTags);
        List<Tag> resultTags = tagService.getAllTags();

        assertNotEquals(0, resultTags.size(), "The result list size should not be 0");
    }


    //---GET TAG BY ID---
    //HAPPY PATH
    @Test
    public void testGetTagByIdPass() throws Exception {
        when(tagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        Tag result = tagService.getTagById(1);

        assertEquals(mockTag, result, "The result tag and mock tag should match");

        verify(tagRepository, times(1)).findById(1);
    }

    //SAD PATH - if id not found
    @Test
    public void testGetTagByIdFail() throws Exception {
        assertThrows(Exception.class, ()-> tagService.getTagById(10000)); //id that doesn't exist
    }


    //---ADD TAG---
    //HAPPY PATH
    @Test
    public void testAddTagsPass() throws Exception {
        Blog mockBlog = new Blog();
        List<Tag> mockTags = new ArrayList<>();
        mockBlog.setTags(mockTags);
        when(tagRepository.findByName(anyString())).thenReturn(null); //no duplicates
        when(tagRepository.saveAll(anyList())).thenReturn(mockTagsList);
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog));
        List<Tag> resultTagsList = tagService.addTags(mockTagDTOList);

        assertEquals(mockTagsList, resultTagsList, "The result list should match the mock list");

        verify(tagRepository, times(4)).findByName(anyString()); //4 tags in mock that call this
        verify(tagRepository, times(1)).saveAll(anyCollection());
    }

    //SAD PATH - null tagDTO body
    @Test
    public void testAddTagsNullDTO() {
        assertThrows(NullPointerException.class, ()-> tagService.addTags(null)); //null DTO should throw exception
    }

    //SAD PATH - matching tag found(cannot create duplicates)
    @Test
    public void testAddTagsFail() {
        when(tagRepository.findByName(anyString())).thenReturn(mockTag); //duplicate tag found should throw error
        assertThrows(DuplicateKeyException.class, ()-> tagService.addTags(mockTagDTOList));
    }

    //SAD PATH - blog not found when adding tags with blog id list
    @Test
    public void testCreateTagBlogNotFound() throws Exception {
        when(tagRepository.findByName(anyString())).thenReturn(null); //no duplicates
        when(blogRepository.findById(anyInt())).thenReturn(null); //blog doesn't exist

        assertThrows(Exception.class, ()-> tagService.addTags(mockTagDTOList));
        verify(blogRepository, times(1)).findById(anyInt());
    }



    //---UPDATE TAG---
    //HAPPY PATH
    @Test
    public void testUpdateTagPass() throws Exception {
        when(tagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        when(tagRepository.save(any())).thenReturn(mockTag2);
        Tag updatedTag = tagService.updateTag(1, mockTag2);
        assertEquals(mockTag2, updatedTag, "The result tag and mock tag should match");

        verify(tagRepository, times(1)).findById(1);
        verify(tagRepository, times(1)).save(any());
    }

    //SAD PATH - id not found
    @Test
    public void testUpdateTagFail() {
        assertThrows(Exception.class, ()-> tagService.updateTag(10000, mockTag2)); //id does not exist
    }

    //SAD PATH = null tag body
    @Test
    public void testUpdateTagNull() throws Exception {
        when(tagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        assertThrows(NullPointerException.class, ()-> tagService.updateTag(1, null)); //null tag body provided
    }

    //SAD PATH - tag name exists
    @Test
    public void testUpdateTagNameExists() throws Exception {
        when(tagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        when(tagRepository.findByName(anyString())).thenReturn(mockTag2); //matching tag name exists

        assertThrows(DuplicateKeyException.class, () -> tagService.updateTag(1, mockTag2));
    }

    //---DELETE TAG---
    //HAPPY PATH
    @Test
    public void testDeleteTagPass() throws Exception {
        when(tagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        tagService.deleteTag(1);

        verify(tagRepository, times(1)).findById(1);
        verify(tagRepository, times(1)).delete(mockTag);
    }


    //SAD PATH
    @Test
    public void testDeleteTagFail() {
        assertThrows(Exception.class, ()-> tagService.deleteTag(10000)); //id doesn't exist
    }


    //---GET TAG BY NAME---
    //HAPPY PATH
    @Test
    public void testGetTagByNamePass() throws Exception {
        when(tagRepository.findByName(anyString())).thenReturn(mockTag2);
        Tag result = tagService.getTagByName("test");

        assertEquals(mockTag2, result, "The result tag and mock tag should match");

        verify(tagRepository, times(1)).findByName(anyString());
    }

    //SAD PATH - tag name is blank
    @Test
    public void testGetTagByNameEmptyParam() throws Exception {
        assertThrows(IllegalArgumentException.class, ()-> tagService.getTagByName(""));
    }


    //---GET BLOGS BY TAG ID---
    //HAPPY PATH
    @Test
    public void testGetBlogsByTagPass() throws Exception {
        List<Blog> mockBlogs = new ArrayList<>();
        mockTag.setBlogs(mockBlogs);
        when(tagRepository.findById(1)).thenReturn(Optional.of(mockTag));

        List<Blog> resultList = tagService.getBlogsByTag(1);
        assertEquals(mockBlogs, resultList, "The result list and mock list should match");

        verify(tagRepository, times(1)).findById(1);
    }

    //SAD PATH
    @Test
    public void testGetBlogsByTagFail() throws Exception {
        when(tagRepository.findById(1)).thenReturn(null);
        assertThrows(Exception.class, ()-> tagService.getBlogsByTag(1));
    }


    //---MAP DTO TO TAG OBJECT---
    //SAD PATH (happy path mostly covered by tag creation methods)
    @Test
    public void testMapToTags() throws Exception {
        List<Integer> blogIds = Arrays.asList(100001, 100002); //mock IDs do not exist
        mockTagDTOList.getFirst().setBlogIds(blogIds);
        when(blogRepository.findById(anyInt())).thenReturn(null); //no blog match found

        assertThrows(Exception.class, ()-> tagService.mapToTags(mockTagDTOList));
    }

}
