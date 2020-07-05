package com.asia.tokyo.service;

import com.asia.tokyo.controller.model.CustomerDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CustomerService {

    CustomerDto addCustomer(CustomerDto customerDto);

    CustomerDto findCustomerById(UUID uuid);

    CustomerDto updateCustomer(CustomerDto customerDto);

    void deleteCustomer(UUID uuid);

    List<CustomerDto> findAllByCustomerNameLike(String customerName);

    Set<CustomerDto> findAll();
}
