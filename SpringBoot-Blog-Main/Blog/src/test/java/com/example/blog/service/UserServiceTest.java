package com.example.blog.service;

import com.example.blog.dto.NotificationDTO;
import com.example.blog.model.Address;
import com.example.blog.model.Blog;
import com.example.blog.model.User;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    IUserRepository userRepository;

    @MockBean
    IBlogRepository blogRepository;

    @MockBean
    RestTemplate restTemplate;

    private User createMockUser(Integer id, String username, String email, String password, LocalDate date) {
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setUsername(username);
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setRegistrationDate(date);
        mockUser.setAddress(new Address(1, "123 1st Street", "Austin", "Texas", "78701", "USA", mockUser));

        return mockUser;
    }

    private User mockUser = createMockUser(1, "TestUsername1", "test@email.com", "password123",
            LocalDate.of(2023, 1, 1));
    private User mockUser2 = createMockUser(2, "TestUsername2", "example@email.com", "password321",
            LocalDate.of(2022, 5, 15));

    //---GET ALL USERS---
    //HAPPY PATH
    @Test
    public void testGetAllCustomersPass() {

        List<User> mockList = Arrays.asList(mockUser, mockUser2);

        when(userRepository.findAll()).thenReturn(mockList); //expected
        List<User> resultList = userService.getAllUsers(); //actual

        assertEquals(2, resultList.size(), "The result list size should be 2");
        assertEquals(mockList, resultList, "The result list and mock list should match");

        verify(userRepository, times(1)).findAll(); //verify mock repo called once
    }

    //SAD PATH
    @Test
    public void testGetAllUsersFail(){
        List<User> mockList = Arrays.asList(mockUser, mockUser2);

        when(userRepository.findAll()).thenReturn(mockList); //expected
        List<User> resultList = userService.getAllUsers(); //actual

        assertNotEquals(5, resultList.size(),"The result list size should not be 5");
    }


    //---GET USER BY ID---
    //HAPPY PATH
    @Test
    public void testGetUserById() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        User result = userService.getUserById(1);

        assertEquals(mockUser, result, "The result user and mock user should match");

        verify(userRepository, times(1)).findById(1);
    }

    //SAD PATH
    @Test
    public void testGetUserByIdFail() throws Exception {
        assertThrows(Exception.class, () -> userService.getUserById(10000)); //this id does not exist & should throw an exception
    }


    //---ADD USER---
    //HAPPY PATH
    @Test
    public void testCreateUserPass() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        User result = userService.createUser(mockUser);

        assertNotNull(result, "The result user should not be null");
        assertEquals(mockUser, result, "The result user and mock user should match");
        verify(userRepository, times(1)).save(any(User.class));
    }

    //SAD PATH - null user body
    @Test
    public void testCreateUserFail() throws Exception {
        assertThrows(Exception.class, () -> userService.createUser(null)); //cannot create null customer
    }

    //SAD PATH - username exists
    @Test
    public void testCreateUserUsernameExist() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);

        assertThrows(DuplicateKeyException.class, () -> userService.createUser(mockUser2));
    }


    //---UPDATE USER---
    //HAPPY PATH
    @Test
    public void testUpdateUserPass() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser2);
        User updatedUser = userService.updateUser(1, mockUser2);

        assertEquals(mockUser2, updatedUser, "The result user and mock user 2 should match");
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
    }


    //SAD PATH 1 - null user body
    @Test
    public void testUpdateUserNull() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        assertThrows(Exception.class, () -> userService.updateUser(1, null)); //null user body provided
    }

    //SAD PATH 2 - user id not found
    @Test
    public void testUpdateUserFail() throws Exception {
        assertThrows(Exception.class, () -> userService.updateUser(10000, mockUser2)); //id doesn't exist
    }

    //SAD PATH 3 - username exists
    @Test
    public void testUpdateUserUsernameExists() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser2);

        assertThrows(DuplicateKeyException.class, () -> userService.updateUser(1, mockUser));
    }


    //---DELETE USER---
    //HAPPY PATH
    @Test
    public void testDeleteUserPass() throws Exception {
       when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        userService.deleteUser(1);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).delete(mockUser);
    }


    //SAD PATH - user id not found
    @Test
    public void testDeleteUserFail() throws Exception {
        assertThrows(Exception.class, () -> userService.deleteUser(10000)); //id doesn't exist
    }


    //---ADD BLOG TO USER---
    //HAPPY PATH
    @Test
    public void testAddBlogToUserPass() throws Exception {
        Blog mockBlog = new Blog();
        mockBlog.setTitle("Test Title");
        mockBlog.setContent("Random content");
        mockBlog.setLikes(200);

        List<Blog> mockBlogList = new ArrayList<>();
        mockUser.setBlogs(mockBlogList);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        userService.addBlogToUser(1, mockBlog);

        assertEquals(mockUser, mockBlog.getUser(), "The result user of the blog and mock user should match");
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(mockUser);
        verify(blogRepository, times(1)).save(mockBlog);
    }

    //SAD PATH - user doesn't exist
    @Test
    public void testAddBlogToUserFail() throws Exception {
        Blog mockBlog = new Blog();
        mockBlog.setTitle("Test Title");
        mockBlog.setContent("Random content");
        mockBlog.setLikes(200);

        assertThrows(Exception.class, () -> userService.addBlogToUser(10000, mockBlog)); //user id doesnt exist
    }

    //SAD PATH - null blog
    @Test
    public void testAddBlogToUserNull() throws Exception {
        List<Blog> mockBlogList = new ArrayList<>();
        mockUser.setBlogs(mockBlogList);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); //mock user exists

        assertThrows(NullPointerException.class, () -> userService.addBlogToUser(1, null));
    }



    //---GET BLOGS BY USER ID---
    //HAPPY PATH
    @Test
    public void testGetBlogsByUserPass() throws Exception {
        List<Blog> mockBlogList = new ArrayList<>();
        mockUser.setBlogs(mockBlogList);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        List<Blog> resultBlogs = userService.getBlogsByUserId(1);

        assertEquals(mockBlogList, resultBlogs, "The result blogs and mock blogs list should match");
        verify(userRepository, times(1)).findById(1);
    }

    //SAD PATH
    @Test
    public void testGetBlogsByUserFail() throws Exception {
        assertThrows(Exception.class, () -> userService.getBlogsByUserId(10000)); //id doesn't exist
    }

    //---GET NOTIFICATIONS BY USER ID---
    //HAPPY PATH
    @Test
    public void testGetNotificationsPass() throws Exception {
        NotificationDTO[] notificationDTOArray = new NotificationDTO[1];
        ResponseEntity<NotificationDTO[]> response = new ResponseEntity<>(notificationDTOArray, HttpStatus.OK);
        List<NotificationDTO> mockNotificationDTOList = Arrays.asList(notificationDTOArray);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));
        when(restTemplate.getForEntity("http://localhost:8081/notifications/users/{id}",
                NotificationDTO[].class,
                mockUser.getId())).thenReturn(response);
        List<NotificationDTO> resultList = userService.getNotificationsByUserId(1);

        assertEquals(mockNotificationDTOList, resultList, "The result list and mock list should match");

        verify(userRepository, times(1)).findById(anyInt());

    }

    //SAD PATH - any non-2xx status code from response
    @Test
    public void testGetNotificationsFail() throws Exception {
        NotificationDTO[] notificationDTOArray = new NotificationDTO[1];
        ResponseEntity<NotificationDTO[]> response = new ResponseEntity<>(notificationDTOArray, HttpStatus.NOT_FOUND);
        List<NotificationDTO> mockNotificationDTOList = Arrays.asList(notificationDTOArray);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));
        when(restTemplate.getForEntity("http://localhost:8081/notifications/users/{id}",
                NotificationDTO[].class,
                mockUser.getId())).thenReturn(response);

        assertThrows(Exception.class, () -> userService.getNotificationsByUserId(1));
    }

    //SAD PATH - user id doesn't exist
    @Test
    public void testGetNotificationsUserFail() throws Exception {
        assertThrows(Exception.class, () -> userService.getNotificationsByUserId(10000)); //id doesnt exist
    }

}
