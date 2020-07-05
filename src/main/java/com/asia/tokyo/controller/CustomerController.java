package com.asia.tokyo.controller;

import com.asia.tokyo.domain.Customer;
import com.asia.tokyo.service.CustomerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/add")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.addCustomer(customer), httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/get/{uuid}")
    public ResponseEntity<Customer> findCustomerById(@PathVariable UUID uuid) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.findCustomerById(uuid), httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Customer> updateAdmin(@RequestBody Customer customer) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.updateCustomer(customer), httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID uuid) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        customerService.deleteCustomer(uuid);
        return ResponseEntity.noContent().headers(httpHeaders).build();
    }

    @GetMapping("/all/{customerName}")
    public ResponseEntity<List<Customer>> findAllByCustomerNameLike(@PathVariable String customerName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.findAllByCustomerNameLike(customerName), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<Customer>> findAll() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.findAll(), httpHeaders, HttpStatus.OK);
    }
}
