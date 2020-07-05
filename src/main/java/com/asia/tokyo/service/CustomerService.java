package com.asia.tokyo.service;

import com.asia.tokyo.domain.Customer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CustomerService {

    Customer addCustomer(Customer customer);

    Customer findCustomerById(UUID uuid);

    Customer updateCustomer(Customer customer);

    void deleteCustomer(UUID uuid);

    List<Customer> findAllByCustomerNameLike(String customerName);

    Set<Customer> findAll();
}
