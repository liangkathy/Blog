package com.example.blog.controller;

import com.example.blog.dto.NotificationDTO;
import com.example.blog.model.Address;
import com.example.blog.model.Blog;
import com.example.blog.model.User;

import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    private Address createAddress(Integer id, String street, String city, String state, String zipCode, String country) {
        Address address = new Address();
        User user = new User();
        address.setId(id);
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setCountry(country);
        address.setUser(user);

        return address;
    }

    private Address mockAddress = createAddress(1, "123 1st Street", "Austin", "Texas", "78701", "USA");

    private User createMockUser(Integer id, String username, String email, String password, LocalDate date) {
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setUsername(username);
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setRegistrationDate(date);
        mockUser.setAddress(mockAddress);

        return mockUser;
    }

    private User mockUser = createMockUser(1, "TestUsername1", "test@email.com", "password123",
            LocalDate.of(2023, 1, 1));
    private User mockUser2 = createMockUser(2, "TestUsername2", "example@email.com", "password321",
            LocalDate.of(2022, 5, 15));


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


    //---GET ALL USERS---
    //HAPPY PATH
    @Test
    public void testGetAllUsersPass() throws Exception {

        List<User> mockList = Arrays.asList(mockUser, mockUser2);

        String jsonUserList = convertToJSON(mockList);

        when(userService.getAllUsers()).thenReturn(mockList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUserList));

        verify(userService, times(1)).getAllUsers();
    }

    //---GET USER BY ID---
    //HAPPY PATH
    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1)).thenReturn(mockUser);
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockUser)));

        verify(userService, times(1)).getUserById(1);
    }



    //SAD PATH - id does not exist
    @Test
    public void testGetUserByIdFail() throws Exception {
        when(userService.getUserById(1000)).thenThrow(new Exception("User not found")); //giving id that does not exist
        mockMvc.perform(get("/users/{id}", 1000))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(1000); //verify call to nonexistent id was done
    }


    //---ADD USER---
    //HAPPY PATH
    @Test
    public void testCreateUserPass() throws Exception {
        String jsonUser = convertToJSON(mockUser);

        when(userService.createUser(any(User.class))).thenReturn(mockUser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonUser));

        verify(userService, times(1)).createUser(any(User.class));

    }


    //SAD PATH - user response body is empty/null
    @Test
    public void testCreateUserFail() throws Exception {
        when(userService.createUser(null)).thenThrow(new NullPointerException("User cannot be null"));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    //SAD PATH 2 - username exists
    @Test
    public void testCreateUserUsernameExists() throws Exception {
        User mockUser3 = createMockUser(1, "TestUsername1", "test@email.com", "password123",
                LocalDate.of(2023, 1, 1));
        String jsonUser3 = convertToJSON(mockUser3);

        when(userService.createUser(any(User.class))).thenThrow(new DuplicateKeyException("Username already exists"));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser3))
                .andExpect(status().isConflict());

        verify(userService, times(1)).createUser(any(User.class));
    }


    //---UPDATE USER---
    //HAPPY PATH
    @Test
    public void testUpdateUserPass() throws Exception {
        mockUser.setId(mockUser2.getId()); //this is not run in service > just for testing
        mockUser.setRegistrationDate(mockUser2.getRegistrationDate()); //this is not run in service > just for testing

        mockUser.setUsername(mockUser2.getUsername());
        mockUser.setEmail(mockUser2.getEmail());
        mockUser.setPassword(mockUser2.getPassword());
        mockUser.setAddress(mockUser2.getAddress());


        String jsonUser2 = convertToJSON(mockUser2);

        when(userService.updateUser(anyInt(),any(User.class))).thenReturn(mockUser);
        mockMvc.perform(put("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser2))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonUser2));

        verify(userService, times(1)).updateUser(anyInt(), any(User.class));
    }


    //SAD PATH - null user in response body
    @Test
    public void testUpdateUserNull() throws Exception {
        when(userService.updateUser(1, null)).thenThrow(new NullPointerException("User cannot be null"));
        mockMvc.perform(put("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }


    //SAD PATH 2 - id does not exist to update
    @Test
    public void testUpdateUserFail() throws Exception {
        when(userService.updateUser(anyInt(), any(User.class))).thenThrow(new Exception("User not found"));
        mockMvc.perform(put("/users/{id}", 10000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockUser)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(anyInt(),any(User.class));
    }

    //SAD PATH 3 - username already exists
    @Test
    public void testUpdateUserUsernameExists() throws Exception {
        when(userService.updateUser(anyInt(), any(User.class))).thenThrow(new DuplicateKeyException("Username already exists"));
        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJSON(mockUser2)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).updateUser(anyInt(), any(User.class));
    }


    //---DELETE USER---
    //HAPPY PATH
    @Test
    public void testDeleteUserPass() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1);
    }

    //SAD PATH - id does not exist
    @Test
    public void testDeleteUserFail() throws Exception {
        doThrow(new Exception("User not found")).when(userService).deleteUser(10000);
        mockMvc.perform(delete("/users/{id}", 10000))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(10000);
    }


    //---ADD BLOG TO USER---
    //HAPPY PATH
    @Test
    public void testAddBlogToUserPass() throws Exception {
        Blog mockBlog = new Blog();
        mockBlog.setTitle("Test Title");
        mockBlog.setContent("Random content");
        mockBlog.setLikes(200);
        mockBlog.setUser(mockUser);

        List<Blog> mockBlogList = new ArrayList<>();
        mockUser.setBlogs(mockBlogList);
        mockUser.getBlogs().add(mockBlog);

        when(userService.addBlogToUser(anyInt(), any(Blog.class))).thenReturn(mockUser);
        mockMvc.perform(put("/users/{id}/blogs", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockBlog)))
                .andExpect(status().isOk())
                .andExpect(content().string(convertToJSON(mockUser)));

        verify(userService, times(1)).addBlogToUser(anyInt(), any(Blog.class));
    }


    //SAD PATH - blog is null
    @Test
    public void testAddBlogToUserNullBlog() throws Exception {
        when(userService.addBlogToUser(1,null)).thenThrow(new NullPointerException("Blog cannot be null"));
        mockMvc.perform(put("/users/{id}/blogs", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    //SAD PATH 2 - user id not found
    @Test
    public void testAddBlogToUserFail() throws Exception {
        Blog mockBlog = new Blog();
        mockBlog.setTitle("Test Title");
        mockBlog.setContent("Random content");
        mockBlog.setLikes(200);

        when(userService.addBlogToUser(10000, mockBlog)).thenThrow(new Exception("User not found"));
        mockMvc.perform(put("/users/{id}/blogs", 10000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockBlog)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).addBlogToUser(anyInt(), any(Blog.class));
    }

    //---GET ALL BLOGS BY USER ID---
    //HAPPY PATH
    @Test
    public void testGetAllBlogsByUserPass() throws Exception {
        List<Blog> mockBlogList = new ArrayList<>();
        mockUser.setBlogs(mockBlogList);

        when(userService.getBlogsByUserId(anyInt())).thenReturn(mockBlogList);
        mockMvc.perform(get("/users/{id}/blogs", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockBlogList)));

        verify(userService, times(1)).getBlogsByUserId(anyInt());
    }

    //SAD PATH - user not found
    @Test
    public void testGetAllBlogsByUserFail() throws Exception {
        when(userService.getBlogsByUserId(anyInt())).thenThrow(new Exception("User not found"));
        mockMvc.perform(get("/users/{id}/blogs", 10000))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getBlogsByUserId(anyInt());
    }


    //---GET NOTIFICATIONS---
    //HAPPY PATH
    @Test
    public void testGetNotificationsPass() throws Exception {
        List<NotificationDTO> mockNotificationDTOList = new ArrayList<>();
        when(userService.getNotificationsByUserId(1)).thenReturn(mockNotificationDTOList);
        mockMvc.perform(get("/users/{id}/notifications", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockNotificationDTOList)));

        verify(userService, times(1)).getNotificationsByUserId(1);
    }

    //SAD PATH
    @Test
    public void testGetNotificationsFail() throws Exception {
        when(userService.getNotificationsByUserId(10000)).thenThrow(new Exception("User not found"));
        mockMvc.perform(get("/users/{id}/notifications", 10000))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getNotificationsByUserId(10000);
    }


}
