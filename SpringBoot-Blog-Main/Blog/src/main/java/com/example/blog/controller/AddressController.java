package com.example.blog.controller;

import com.example.blog.model.Address;
import com.example.blog.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@CrossOrigin(origins = "*")
public class AddressController {
    @Autowired
    AddressService addressService;

    //GET
    //get all addresses
    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    //get address by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(addressService.getAddressById(id));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if address id not found
        }
    }

    //POST
    //add address
    @PostMapping
    public ResponseEntity<?> createAddress(@Valid @RequestBody Address address) {
        try {
            return new ResponseEntity<>(addressService.createAddress(address), HttpStatus.CREATED);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if address body is null
        }
    }


    //PUT
    //update address
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Integer id, @Valid @RequestBody Address address) {
        try {
            return ResponseEntity.ok(addressService.updateAddress(id,address));
        }catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //if address body is null
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if address id not found
        }
    }

    //DELETE
    //delete address  - can't delete an address tied to a user without deleting user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer id) {
        try{
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //if address id not found
        }
    }
}
