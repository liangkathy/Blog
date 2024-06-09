package com.example.blog.service;

import com.example.blog.dto.BlogDTO;
import com.example.blog.model.*;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.ICommentRepository;
import com.example.blog.repository.ITagRepository;
import com.example.blog.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BlogServiceTest {
    @Autowired
    BlogService blogService;

    @MockBean
    IBlogRepository blogRepository;

    @MockBean
    IUserRepository userRepository;

    @MockBean
    ITagRepository tagRepository;

    @MockBean
    ICommentRepository commentRepository;

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
        User newUser = createMockUser(1, "TestUsername1", "test@email.com", "password123",
                LocalDate.of(2023, 1, 1));
        blog.setUser(newUser);

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

    private User createMockUser(Integer id, String username, String email, String password, LocalDate date) {
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setUsername(username);
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setRegistrationDate(date);
        Address address = new Address();
        mockUser.setAddress(address);

        return mockUser;
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

    private User mockUser = createMockUser(1, "TestUsername1", "test@email.com", "password123",
            LocalDate.of(2023, 1, 1));
    private Tag mockTag = createMockTag(1, "Tag Title", "Tag Description");
    private Tag mockTag2 = createMockTag(2, "Tag Title2", "Tag Description2");
    private Comment mockComment = createComment(1, "Text", 25, "TestUsername1");

    //---GET ALL BLOGS---
    //HAPPY PATH
    @Test
    public void testGetAllBlogsPass(){
        List<Blog> mockBlogs = Arrays.asList(mockBlog, mockBlog2);

        when(blogRepository.findAll()).thenReturn(mockBlogs);
        List<Blog> resultBlogs = blogService.getAllBlogs();

        assertEquals(mockBlogs, resultBlogs, "The result list and mock list should match");
        assertEquals(2,resultBlogs.size(),"The result list size should be 2");

        verify(blogRepository, times(1)).findAll();
    }

    //SAD PATH
    @Test
    public void testGetAllBlogsFail(){
        List<Blog> mockBlogs = Arrays.asList(mockBlog, mockBlog2);

        when(blogRepository.findAll()).thenReturn(mockBlogs);
        List<Blog> resultBlogs = blogService.getAllBlogs();

        assertNotEquals(5,resultBlogs.size(),"The result list size should not be 5");
    }

    //---GET BLOG BY ID---
    //HAPPY PATH
    @Test
    public void testGetBlogByIdPass() throws Exception {
        when(blogRepository.findById(1)).thenReturn(Optional.of(mockBlog));
        Blog result = blogService.getBlogById(1);

        assertEquals(mockBlog, result, "The result blog and mock blog should match");

        verify(blogRepository, times(1)).findById(1);
    }

    //SAD PATH
    @Test
    public void testGetBlogByIdFail() throws Exception {
        assertThrows(Exception.class, () -> blogService.getBlogById(10000)); //id doesn't exist
    }

    //---ADD BLOG---
    //HAPPY PATH
    @Test
    public void testCreateBlogPass() throws Exception {
        List<Blog> mockBlogs = new ArrayList<>();
        mockUser.setBlogs(mockBlogs);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); //user exists
        when(tagRepository.findById(anyInt())).thenReturn(Optional.of(mockTag)); //tag exists
        when(blogRepository.save(any(Blog.class))).thenReturn(mockBlog);
        Blog result = blogService.createBlog(mockBlogDTO);

        assertEquals(mockBlog, result, "The result blog and mock blog should match");

        verify(userRepository, times(1)).findById(1);
        verify(tagRepository, times(2)).findById(anyInt()); //two mock tags, called this twice
        verify(blogRepository, times(1)).save(any(Blog.class));
    }

    //SAD PATH - user not found
    @Test
    public void testCreateBlogFail() throws Exception {
        assertThrows(Exception.class, () -> blogService.createBlog(mockBlogDTO)); //no user id in db, will fail
    }

    //---UPDATE BLOG---
    //HAPPY PATH
    @Test
    public void testUpdateBlogPass() throws Exception {
        mockBlog.setId(mockBlog2.getId()); //for testing only
        mockBlog.setCreationDate(mockBlog2.getCreationDate()); //for testing only
        mockBlog.setLastModifiedDate(mockBlog2.getLastModifiedDate()); //for testing only

        when(blogRepository.findById(1)).thenReturn(Optional.of(mockBlog));
        when(blogRepository.save(any(Blog.class))).thenReturn(mockBlog);
        Blog updatedBlog = blogService.updateBlog(1,mockBlog2);

        assertEquals(mockBlog, updatedBlog, "The result blog and mock blog should match");

        verify(blogRepository, times(1)).findById(1);
        verify(blogRepository, times(1)).save(any(Blog.class));
    }


    //SAD PATH - blog not found
    @Test
    public void testUpdateBlogFail() throws Exception {
        assertThrows(Exception.class, () -> blogService.updateBlog(10000,mockBlog2)); //blog id doesnt exist
    }

    //SAD PATH - null blog
    @Test
    public void testUpdateBlogNull() throws Exception {
        when(blogRepository.findById(1)).thenReturn(Optional.of(mockBlog));
        assertThrows(NullPointerException.class, () -> blogService.updateBlog(1,null)); //null blog body in request
    }

    //---DELETE BLOG---
    //HAPPY PATH
    @Test
    public void testDeleteBlogPass() throws Exception {
        when(blogRepository.findById(1)).thenReturn(Optional.of(mockBlog));
        blogService.deleteBlog(1);

        verify(blogRepository, times(1)).findById(1);
        verify(blogRepository, times(1)).delete(mockBlog);
    }


    //SAD PATH - blog not found
    @Test
    public void testDeleteBlogFail() throws Exception {
        assertThrows(Exception.class, () -> blogService.deleteBlog(10000)); //blog id doesnt exist

    }


    //---ADD COMMENT TO BLOG---
    //HAPPY PATH
    @Test
    public void testAddCommentToBlogPass() throws Exception {
        when(blogRepository.findById(1)).thenReturn(Optional.of(mockBlog)); //mock blog exists
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser); //mock user exists
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);
        when(blogRepository.save(any(Blog.class))).thenReturn(mockBlog);

        Blog result = blogService.addCommentToBlog(1,mockComment);

        assertEquals(mockBlog, result, "The result blog and mock blog should match");
    }

    //SAD PATH - blog id doesn't exist
    @Test
    public void testAddCommentToBlogFail() throws Exception {
        assertThrows(Exception.class, () -> blogService.addCommentToBlog(10000, mockComment));
    }

    //SAD PATH - username doesn't exist
    @Test
    public void testAddCommentToBlogUserFail() throws Exception {
        when(blogRepository.findById(1)).thenReturn(Optional.of(mockBlog)); //mock blog exists
        assertThrows(Exception.class, () -> blogService.addCommentToBlog(1, mockComment)); //existing user not mocked
    }

    //SAD PATH null comment
    @Test
    public void testAddCommentToBlogNull() throws Exception {
        when(blogRepository.findById(1)).thenReturn(Optional.of(mockBlog)); //mock blog exists
        assertThrows(NullPointerException.class, () -> blogService.addCommentToBlog(1, null)); //null comment body
    }


    //---GET BLOG BY KEYWORD---
    //HAPPY PATH
    @Test
    public void testGetBlogsByKeywordPass() throws Exception {
        List<Blog> mockBlogs = Arrays.asList(mockBlog, mockBlog2);
        when(blogRepository.findBlogByKeywordInTitleOrContent(anyString())).thenReturn(mockBlogs);
        List<Blog> resultBlogs = blogService.getBlogsByKeyword("blog"); //search is not case-sensitive

        assertEquals(mockBlogs, resultBlogs, "The result list and mock list should match");

        verify(blogRepository, times(1)).findBlogByKeywordInTitleOrContent(anyString());
    }

    //SAD PATH - no results found
    @Test
    public void testGetBlogsByKeywordFail() throws Exception {
        when(blogRepository.findBlogByKeywordInTitleOrContent(anyString())).thenReturn(null); //mock no results found
        assertThrows(Exception.class, () -> blogService.getBlogsByKeyword("blog"));
    }

    //SAD PATH - null keyword
    @Test
    public void testGetBlogsByKeywordNoMatches() throws Exception {
        List<Blog> matchingBlogs = new ArrayList<>(); //empty list
        when(blogRepository.findBlogByKeywordInTitleOrContent("No Match")).thenReturn(matchingBlogs); //mocking no match
        List<Blog> result = blogService.getBlogsByKeyword("No Match");
        assertNull(result, "The result should be null when there is no keyword match");
    }

    //SAD PATH - blank keyword(empty or with whitespace)
    @Test
    public void testGetBlogsByKeywordBlank() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> blogService.getBlogsByKeyword("  ")); //keyword cannot be blank
    }

    //---ADD NEW TAG TO BLOG---
    //HAPPY PATH
    @Test
    public void testAddTagToBlogPass() throws Exception {
        List<Tag> mockTags = new ArrayList<>();
        mockTags.add(mockTag);
        mockTags.add(mockTag2);
        mockBlog.setTags(mockTags);

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog)); //blog exists
        when(tagRepository.findByName(anyString())).thenReturn(null); //no matching tag name exists
        when(blogRepository.save(any(Blog.class))).thenReturn(mockBlog);
        Blog result = blogService.addTagToBlog(1,mockTag);

        assertEquals(mockBlog, result, "The result tag and mock tag should match");

        verify(blogRepository, times(1)).findById(1);
        verify(tagRepository, times(1)).findByName(anyString());
    }

    //SAD PATH - blog not found
    @Test
    public void testAddTagToBlogFail() throws Exception {
        assertThrows(Exception.class, () -> blogService.addTagToBlog(10000, mockTag));
    }

    //SAD PATH - tag name exists
    @Test
    public void testAddTagToBlogTagNameExists() throws Exception {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog));
        when(tagRepository.findByName(anyString())).thenReturn(mockTag2);

        assertThrows(DuplicateKeyException.class, ()-> blogService.addTagToBlog(1,mockTag));
    }

    //SAD PATH - tag null
    @Test
    public void testAddTagToBlogTagNull() throws Exception {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog));

        assertThrows(NullPointerException.class, () -> blogService.addTagToBlog(1,null));
    }


    //---REMOVE TAG FROM BLOG---
//HAPPY PATH
    @Test
    public void testRemoveTagFromBlogPass() throws Exception {
        List<Tag> mockTags = new ArrayList<>();
        mockTags.add(mockTag);
        mockTags.add(mockTag2);
        mockBlog.setTags(mockTags);

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog));
        when(tagRepository.findById(anyInt())).thenReturn(Optional.of(mockTag2));
        when(blogRepository.save(any(Blog.class))).thenReturn(mockBlog);
        Blog result = blogService.removeTagFromBlog(1,1);

        assertEquals(mockBlog, result, "The result blog and mock blog should match");

        verify(blogRepository, times(1)).findById(anyInt());
        verify(tagRepository, times(1)).findById(anyInt());
    }


    //SAD PATH - blog id not found
    @Test
    public void testRemoveTagFromBlogFail() throws Exception {
        assertThrows(Exception.class, () -> blogService.removeTagFromBlog(10000,1));
    }

    //SAD PATH - tag id not found
    @Test
    public void testRemoveTagFromBlogNull() throws Exception {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(mockBlog));
        assertThrows(Exception.class, () -> blogService.removeTagFromBlog(1,10000));
    }
}
