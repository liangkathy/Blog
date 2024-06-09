package com.example.blog.service;

import com.example.blog.model.Address;
import com.example.blog.repository.IAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    IAddressRepository addressRepository;

    //get all addresses
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    //get address by id
    public Address getAddressById(Integer id) throws Exception {
        return addressRepository.findById(id).orElseThrow(() -> new Exception("Address with id " + id + " not found"));
    }

    //add address
    public Address createAddress(Address address) {
        if (address != null){
            return addressRepository.save(address);
        } else {
            throw new NullPointerException("Address cannot be null");
        }
    }

    //update address
    public Address updateAddress(Integer id, Address address) throws Exception {
        Address existingAddress = addressRepository.findById(id).orElseThrow(() -> new Exception("Address with id " + id + " not found"));
        if (address != null){
            existingAddress.setStreet(address.getStreet());
            existingAddress.setCity(address.getCity());
            existingAddress.setState(address.getState());
            existingAddress.setZipCode(address.getZipCode());
            existingAddress.setCountry(address.getCountry());
            return addressRepository.save(existingAddress);
        } else {
            throw new NullPointerException("Address cannot be null");
        }
    }

    //delete address  - can't delete an address tied to a user without deleting user
    public void deleteAddress(Integer id) throws Exception {
        Address existingAddress = addressRepository.findById(id).orElseThrow(() -> new Exception("Address with id " + id + " not found"));
        addressRepository.delete(existingAddress);
    }
}
