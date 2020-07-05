package com.asia.tokyo.service;

import com.asia.tokyo.exception.CustomerException;
import com.asia.tokyo.domain.Customer;
import com.asia.tokyo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer addCustomer(Customer customer) throws CustomerException {
        if(customer == null)
            throw new CustomerException("The customer informations were not provided.");
        return customerRepository.save(customer);
    }

    @Override
    public Customer findCustomerById(UUID uuid) throws CustomerException {
        if(uuid == null)
            throw new CustomerException("This UUID is not valid.");
        Customer result = customerRepository.findById(uuid).orElse(null);
        if(result == null)
            throw new CustomerException("This UUID is unknow.");
        return result;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if(customer == null)
            throw new CustomerException("The customer informations were not provided.");
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(UUID uuid) {
        if(uuid == null)
            throw new CustomerException("This UUID is not valid.");
        if(!customerRepository.existsById(uuid))
            throw new CustomerException("That UUID is unknown.");
        customerRepository.deleteById(uuid);
    }

    @Override
    public List<Customer> findAllByCustomerNameLike(String customerName) {
        if(customerName == null)
            throw new CustomerException("The customer name was not provided.");
        return customerRepository.findAllByCustomerNameLike(customerName);
    }

    @Override
    public Set<Customer> findAll() {
        return new HashSet<>(customerRepository.findAll());
    }
}
