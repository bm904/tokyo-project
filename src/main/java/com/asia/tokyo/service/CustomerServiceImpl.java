package com.asia.tokyo.service;

import com.asia.tokyo.controller.mapper.CustomerMapper;
import com.asia.tokyo.controller.model.CustomerDto;
import com.asia.tokyo.domain.Customer;
import com.asia.tokyo.exception.CustomerException;
import com.asia.tokyo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerDto addCustomer(CustomerDto customerDto) throws CustomerException {
        if(customerDto == null)
            throw new CustomerException("The customer informations were not provided.");
        Customer customer = customerMapper.customerDtoToCustomer(customerDto);
        return customerMapper.customerToCustomerDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto findCustomerById(UUID uuid) throws CustomerException {
        if(uuid == null)
            throw new CustomerException("This UUID is not valid.");
        Customer result = customerRepository.findById(uuid).orElse(null);
        if(result == null)
            throw new CustomerException("This UUID is unknow.");
        return customerMapper.customerToCustomerDto(result);
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        if(customerDto == null)
            throw new CustomerException("The customer informations were not provided.");
        Customer customer = customerMapper.customerDtoToCustomer(customerDto);
        return customerMapper.customerToCustomerDto(customerRepository.save(customer));
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
    public List<CustomerDto> findAllByCustomerNameLike(String customerName) {
        if(customerName == null)
            throw new CustomerException("The customer name was not provided.");
        List<Customer> result = customerRepository.findAllByCustomerNameLike(customerName);
        //return result.stream().map( c -> customerMapper.customerToCustomerDto(c)).collect(Collectors.toList());
        return customerMapper.customersToCustomerDtosList(result);
    }

    @Override
    public Set<CustomerDto> findAll() {
        HashSet<Customer> set = new HashSet<Customer>();
        Iterable<Customer> it = customerRepository.findAll();
        for (Customer customer: it)
            set.add(customer);
        //return set.stream().map( c -> customerMapper.customerToCustomerDto(c)).collect(Collectors.toSet());
        return customerMapper.customersToCustomerDtosSet(set);
    }
}
