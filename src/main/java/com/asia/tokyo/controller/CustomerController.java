package com.asia.tokyo.controller;

import com.asia.tokyo.controller.model.CustomerDto;
import com.asia.tokyo.service.CustomerService;
import io.swagger.annotations.Api;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Api("Customer controller")
@Validated
@RestController
@RequestMapping(value = "/api/customer", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.addCustomer(customerDto), httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/get/{uuid}")
    public ResponseEntity<CustomerDto> findCustomerById(@PathVariable UUID uuid) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.findCustomerById(uuid), httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<CustomerDto> updateAdmin(@Valid @RequestBody CustomerDto customerDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.updateCustomer(customerDto), httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID uuid) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        customerService.deleteCustomer(uuid);
        return ResponseEntity.noContent().headers(httpHeaders).build();
    }

    @GetMapping("/all/{customerName}")
    public ResponseEntity<List<CustomerDto>> findAllByCustomerNameLike(@PathVariable String customerName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.findAllByCustomerNameLike(customerName), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<CustomerDto>> findAll() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(customerService.findAll(), httpHeaders, HttpStatus.OK);
    }
}
