package com.example.blog.service;

import com.example.blog.model.Address;
import com.example.blog.model.User;
import com.example.blog.repository.IAddressRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AddressServiceTest {
    @Autowired
    AddressService addressService;

    @MockBean
    IAddressRepository addressRepository;

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

    //---GET ALL ADDRESSES---
    //HAPPY PATH
    @Test
    public void testGetAllAddressesPass() {
        List<Address> mockAddresses = Arrays.asList(mockAddress, mockAddress2);

        when(addressRepository.findAll()).thenReturn(mockAddresses);
        List<Address> resultAddresses = addressService.getAllAddresses();

        assertEquals(mockAddresses,resultAddresses, "The result list and mock list should match");
        assertEquals(2,resultAddresses.size(), "The result list size should be 2");

        verify(addressRepository, times(1)).findAll();
    }

    //SAD PATH
    @Test
    public void testGetAllAddressesFail() {
        List<Address> mockAddresses = Arrays.asList(mockAddress, mockAddress2);
        when(addressRepository.findAll()).thenReturn(mockAddresses);
        List<Address> resultAddresses = addressService.getAllAddresses();

        assertNotEquals(1,resultAddresses.size(), "The result list size should not be 1");
    }



    //---GET ADDRESS BY ID---
    //HAPPY PATH
    @Test
    public void testGetAddressByIdPass() throws Exception {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));
        Address result = addressService.getAddressById(1);

        assertEquals(mockAddress,result, "The result address and mock address should match");

        verify(addressRepository, times(1)).findById(1);
    }

    //SAD PATH - address id doesn't exist
    @Test
    public void testGetAddressByIdFail() throws Exception {
        assertThrows(Exception.class,() -> addressService.getAddressById(10000)); //id doesn't exist
    }


    //---ADD ADDRESS---
    //HAPPY PATH
    @Test
    public void testAddAddressPass() throws Exception {
        when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);
        Address result = addressService.createAddress(mockAddress);

        assertEquals(mockAddress,result, "The result address and mock address should match");

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    //SAD PATH - null address
    @Test
    public void testAddAddressFail() throws Exception {
        assertThrows(NullPointerException.class,() -> addressService.createAddress(null));
    }


    //---UPDATE ADDRESS---
    //HAPPY PATH
    @Test
    public void testUpdateAddressPass() throws Exception {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(mockAddress2);
        Address result = addressService.updateAddress(1,mockAddress2);

        assertEquals(mockAddress2,result, "The result address and mock address should match");

        verify(addressRepository, times(1)).findById(1);
        verify(addressRepository, times(1)).save(any(Address.class));
    }


    //SAD PATH - address not found
    @Test
    public void testUpdateAddressFail() throws Exception {
        assertThrows(Exception.class,() -> addressService.updateAddress(10000,mockAddress2)); // id doesn't exist
    }

    //SAD PATH - address null
    @Test
    public void testUpdateAddressNull() throws Exception {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));
        assertThrows(NullPointerException.class,() -> addressService.updateAddress(1,null)); //null address body
    }

    //---DELETE ADDRESS---
    //HAPPY PATH
    @Test
    public void testDeleteAddressPass() throws Exception {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));
        addressService.deleteAddress(1);

        verify(addressRepository, times(1)).findById(1);
        verify(addressRepository, times(1)).delete(mockAddress);
    }

    //SAD PATH
    @Test
    public void testDeleteAddressFail() throws Exception {
        assertThrows(Exception.class,() -> addressService.deleteAddress(10000)); // id doesn't exist
    }

}
