package com.example.blog.controller;

import com.example.blog.model.Address;
import com.example.blog.model.User;
import com.example.blog.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
public class AddressControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AddressService addressService;

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
    private Address mockAddress2 = createAddress(2, "5700 N Echo Canyon", "Phoenix", "Arizona", "85018", "USA");

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

    //---GET ALL ADDRESSES---
    //HAPPY PATH
    @Test
    public void testGetAllAddresses() throws Exception {
        List<Address> mockAddresses = Arrays.asList(mockAddress, mockAddress2);

        when(addressService.getAllAddresses()).thenReturn(mockAddresses);
        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockAddresses)));

        verify(addressService, times(1)).getAllAddresses();
    }


    //---GET ADDRESS BY ID---
    //HAPPY PATH
    @Test
    public void testGetAddressByIdPass() throws Exception {
        when(addressService.getAddressById(1)).thenReturn(mockAddress);
        mockMvc.perform(get("/addresses/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJSON(mockAddress)));

        verify(addressService, times(1)).getAddressById(1);
    }

    //SAD PATH - id not found
    @Test
    public void testGetAddressByIdFail() throws Exception {
        when(addressService.getAddressById(10000)).thenThrow(new Exception("Address not found"));
        mockMvc.perform(get("/addresses/{id}", 10000))
                .andExpect(status().isNotFound());
    }


    //---ADD ADDRESS---
    //HAPPY PATH
    @Test
    public void testAddAddressPass() throws Exception {
        String jsonAddress = convertToJSON(mockAddress);

        when(addressService.createAddress(any(Address.class))).thenReturn(mockAddress);
        mockMvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAddress))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAddress));

        verify(addressService, times(1)).createAddress(any(Address.class));
    }

    //SAD PATH - null address
    @Test
    public void testAddAddressNull() throws Exception {
        when(addressService.createAddress(null)).thenThrow(new NullPointerException("Address cannot be null"));
        mockMvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }


    //---UPDATE ADDRESS---
    //HAPPY PATH
    @Test
    public void testUpdateAddressPass() throws Exception {
        mockAddress.setId(mockAddress.getId()); //for testing only

        mockAddress.setStreet(mockAddress.getStreet());
        mockAddress.setCity(mockAddress.getCity());
        mockAddress.setState(mockAddress.getState());
        mockAddress.setZipCode(mockAddress.getZipCode());
        mockAddress.setCountry(mockAddress.getCountry());

        String jsonAddress = convertToJSON(mockAddress);
        String jsonAddress2 = convertToJSON(mockAddress2);

        when(addressService.updateAddress(anyInt(), any(Address.class))).thenReturn(mockAddress);
        mockMvc.perform(put("/addresses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAddress2))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonAddress));

        verify(addressService, times(1)).updateAddress(anyInt(), any(Address.class));
    }


    //SAD PATH - id not found
    @Test
    public void testUpdateAddressFail() throws Exception {
        when(addressService.updateAddress(anyInt(), any(Address.class))).thenThrow(new Exception("Address not found"));
        mockMvc.perform(put("/addresses/{id}", 10000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJSON(mockAddress2)))
                .andExpect(status().isNotFound());
    }

    //SAD PATH - address null
    @Test
    public void testUpdateAddressNull() throws Exception {
        when(addressService.updateAddress(1, null)).thenThrow(new NullPointerException("Address cannot be null"));
        mockMvc.perform(put("/addresses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }


    //---DELETE ADDRESS---
    //HAPPY PATH
    @Test
    public void testDeleteAddressPass() throws Exception {
        mockMvc.perform(delete("/addresses/{id}", 1))
                .andExpect(status().isNoContent());
    }

    //SAD PATH
    @Test
    public void testDeleteAddressFail() throws Exception {
        doThrow(new Exception("Address not found")).when(addressService).deleteAddress(1);
        mockMvc.perform(delete("/addresses/{id}", 1))
                .andExpect(status().isNotFound());
    }


}
