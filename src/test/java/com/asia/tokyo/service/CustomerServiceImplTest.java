package com.asia.tokyo.service;

import com.asia.tokyo.controller.mapper.CustomerMapper;
import com.asia.tokyo.controller.model.CustomerDto;
import com.asia.tokyo.domain.Customer;
import com.asia.tokyo.exception.CustomerException;
import com.asia.tokyo.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Test CustomerService")
class CustomerServiceImplTest {
    @InjectMocks
    public CustomerServiceImpl customerService;

    @Mock
    public CustomerRepository customerRepository;

    @Mock
    public CustomerMapper customerMapper;

    @Test
    @DisplayName("Adding a new customer is valid")
    void adding_new_customer_is_valid() {
        // GIVEN
        CustomerDto customerDto = CustomerDto.builder().customerName("James Bond").tableNumber("10").build();
        Customer customer = Customer.builder().customerName("James Bond").tableNumber("10").build();
        when(customerMapper.customerDtoToCustomer(any(CustomerDto.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.customerToCustomerDto(any(Customer.class))).thenReturn(customerDto);

        // WHEN
        CustomerDto result = customerService.addCustomer(customerDto);

        // THEN
        assertEquals(customerDto, result);
    }

    @Test
    @DisplayName("Registering a new customer is valid")
    void adding_empty_customer_is_throwing_customer_exception() {
        // GIVEN WHEN THEN
        assertThrows(CustomerException.class, () -> customerService.addCustomer(null));
    }

    @Test
    @DisplayName("Finding empty UUID customer is throwing customer exception")
    void finding_empty_uuid_is_throwing_customer_exception() {
        // GIVEN WHEN THEN
        assertThrows(CustomerException.class, () -> customerService.findCustomerById(null));
    }

    @Test
    @DisplayName("Finding unknown UUID customer is throwing customer exception")
    void finding_unknown_uuid_is_throwing_customer_exception() {
        // GIVEN
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // WHEN THEN
        assertThrows(CustomerException.class, () -> customerService.findCustomerById(UUID.randomUUID()));
    }


    @Test
    @DisplayName("Finding existing UUID customer is valid")
    void finding_existing_uuid_customer_is_valid() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        CustomerDto customerDto = CustomerDto.builder().id(uuid).customerName("James Bond").tableNumber("10").build();
        Customer customer = Customer.builder().id(uuid).customerName("James Bond").tableNumber("10").build();
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(customer));
        when(customerMapper.customerToCustomerDto(any(Customer.class))).thenReturn(customerDto);

        // WHEN
        CustomerDto result = customerService.findCustomerById(uuid);

        // THEN
        assertEquals(customerDto, result);
    }

    @Test
    @DisplayName("Updating empty customer is throwing customer exception")
    void updating_empty_customer_is_throwing_customer_exception() {
        // GIVEN WHEN THEN
        assertThrows(CustomerException.class, () -> customerService.updateCustomer(null));
    }

    @Test
    @DisplayName("Updating existing customer is valid")
    void updating_existing_customer_is_valid() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        CustomerDto customerDto = CustomerDto.builder().id(uuid).customerName("James Bond").tableNumber("10").build();
        Customer customer = Customer.builder().id(uuid).customerName("James Bond").tableNumber("10").build();

        when(customerMapper.customerDtoToCustomer(any(CustomerDto.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.customerToCustomerDto(any(Customer.class))).thenReturn(customerDto);

        // WHEN
        CustomerDto result = customerService.updateCustomer(customerDto);

        // THEN
        assertEquals(customerDto, result);
    }

    @Test
    @DisplayName("Deleting empty UUID customer is throwing customer exception")
    void deleting_empty_uuid_is_throwing_customer_exception() {
        // GIVEN WHEN THEN
        assertThrows(CustomerException.class, () -> customerService.deleteCustomer(null));
    }

    @Test
    @DisplayName("Deleting unknown UUID customer is throwing customer exception")
    void deleting_unknown_uuid_is_throwing_customer_exception() {
        // GIVEN
        when(customerRepository.existsById(any(UUID.class))).thenReturn(false);

        // WHEN THEN
        assertThrows(CustomerException.class, () -> customerService.deleteCustomer(UUID.randomUUID()));
    }


    @Test
    @DisplayName("Deleting existing UUID customer is valid")
    void deleting_existing_uuid_customer_is_valid() {
        // GIVEN
        when(customerRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(customerRepository).deleteById(any(UUID.class));

        // WHEN
        customerService.deleteCustomer(UUID.randomUUID());
    }

    @Test
    @DisplayName("Finding customers by 'name like' is giving 2 customer records")
    void finding_customers_by_name_like_is_giving_2_customer_records() {
        // GIVEN
        String customerNamePattern = "James";
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Customer customer1 = Customer.builder().id(uuid1).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(uuid2).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> filteredCustomer =  Arrays.asList(customer1, customer2);

        CustomerDto customerDto1 = CustomerDto.builder().id(uuid1).customerName("James Bond").tableNumber("10").build();
        CustomerDto customerDto2 = CustomerDto.builder().id(uuid2).customerName("James-Lee Dog").tableNumber("8").build();
        List<CustomerDto> filteredCustomerDtos =  Arrays.asList(customerDto1, customerDto2);

        when(customerRepository.findAllByCustomerNameLike(any(String.class))).thenReturn(filteredCustomer);
        when(customerMapper.customersToCustomerDtosList(any(List.class))).thenReturn(filteredCustomerDtos);

        // WHEN
        List<CustomerDto> result = customerService.findAllByCustomerNameLike(customerNamePattern);

        // THEN
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Finding customers by 'name like' is giving 0 customer records")
    void finding_customers_by_name_like_is_giving_0_customer_records() {
        // GIVEN
        String customerNamePattern = "Lucy";
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Customer customer1 = Customer.builder().id(uuid1).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(uuid2).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> filteredCustomer =  Arrays.asList(customer1, customer2);

        List<CustomerDto> filteredCustomerDtos =  Arrays.asList();

        when(customerRepository.findAllByCustomerNameLike(any(String.class))).thenReturn(filteredCustomer);
        when(customerMapper.customersToCustomerDtosList(any(List.class))).thenReturn(filteredCustomerDtos);

        // WHEN
        List<CustomerDto> result = customerService.findAllByCustomerNameLike(customerNamePattern);

        // THEN
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Finding all customers is giving 4 records")
    void finding_all_customers_is_giving_4_records() {
        // GIVEN
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Customer customer1 = Customer.builder().id(uuid1).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(uuid2).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> customers =  Arrays.asList(customer1, customer2);

        CustomerDto customerDto1 = CustomerDto.builder().id(uuid1).customerName("James Bond").tableNumber("10").build();
        CustomerDto customerDto2 = CustomerDto.builder().id(uuid2).customerName("James-Lee Dog").tableNumber("8").build();
        Set<CustomerDto> customersDto =  new HashSet<>(Arrays.asList(customerDto1, customerDto2));

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.customersToCustomerDtosSet(any(Set.class))).thenReturn(customersDto);

        // WHEN
        Set<CustomerDto> result = customerService.findAll();

        // THEN
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Finding all customers is giving 0 records")
    void finding_all_customers_is_giving_0_records() {
        // GIVEN
        when(customerRepository.findAll()).thenReturn(Arrays.asList());

        // WHEN
        Set<CustomerDto> result = customerService.findAll();

        // THEN
        assertEquals(0, result.size());
    }

}