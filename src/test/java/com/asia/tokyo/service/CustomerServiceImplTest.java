package com.asia.tokyo.service;

import com.asia.tokyo.exception.CustomerException;
import com.asia.tokyo.domain.Customer;
import com.asia.tokyo.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Test CustomerService")
class CustomerServiceImplTest {
    @InjectMocks
    public CustomerServiceImpl customerService;

    @Mock
    public CustomerRepository customerRepository;

    @Test
    @DisplayName("Registering a new customer is valid")
    void adding_new_customer_is_valid() {
        // GIVEN
        Customer customer = Customer.builder().customerName("James Bond").tableNumber("10").build();
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // WHEN
        Customer result = customerService.addCustomer(customer);

        // THEN
        assertEquals(customer, result);
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
        Customer customer = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(customer));

        // WHEN
        Customer result = customerService.findCustomerById(customer.getId());

        // THEN
        assertEquals(customer, result);
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
        Customer customer = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // WHEN
        Customer result = customerService.updateCustomer(customer);

        // THEN
        assertEquals(customer, result);
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
        Customer customer1 = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).customerName("Marc Lee").tableNumber("2").build();
        Customer customer3 = Customer.builder().id(UUID.randomUUID()).customerName("Anna Smith").tableNumber("5").build();
        Customer customer4 = Customer.builder().id(UUID.randomUUID()).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> customers = Arrays.asList(customer1, customer2, customer3, customer4);
        List<Customer> filteredCustomer = customers.stream().filter(c -> c.getCustomerName().contains(customerNamePattern)).collect(Collectors.toList());

        when(customerRepository.findAllByCustomerNameLike(any(String.class))).thenReturn(filteredCustomer);

        // WHEN
        List<Customer> result = customerService.findAllByCustomerNameLike(customerNamePattern);

        // THEN
        assertEquals(filteredCustomer, result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Finding customers by 'name like' is giving 0 customer records")
    void finding_customers_by_name_like_is_giving_0_customer_records() {
        // GIVEN
        String customerNamePattern = "Lucy";
        Customer customer1 = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).customerName("Marc Lee").tableNumber("2").build();
        Customer customer3 = Customer.builder().id(UUID.randomUUID()).customerName("Anna Smith").tableNumber("5").build();
        Customer customer4 = Customer.builder().id(UUID.randomUUID()).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> customers = Arrays.asList(customer1, customer2, customer3, customer4);
        List<Customer> filteredCustomer = customers.stream().filter(c -> c.getCustomerName().contains(customerNamePattern)).collect(Collectors.toList());

        when(customerRepository.findAllByCustomerNameLike(any(String.class))).thenReturn(filteredCustomer);

        // WHEN
        List<Customer> result = customerService.findAllByCustomerNameLike(customerNamePattern);

        // THEN
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Finding all customers is giving 4 records")
    void finding_all_customers_is_giving_4_records() {
        // GIVEN
        String customerNamePattern = "James";
        Customer customer1 = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).customerName("Marc Lee").tableNumber("2").build();
        Customer customer3 = Customer.builder().id(UUID.randomUUID()).customerName("Anna Smith").tableNumber("5").build();
        Customer customer4 = Customer.builder().id(UUID.randomUUID()).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> customers = Arrays.asList(customer1, customer2, customer3, customer4);

        when(customerRepository.findAll()).thenReturn(customers);

        // WHEN
        Set<Customer> result = customerService.findAll();

        // THEN
        assertEquals(new HashSet<>(customers), result);
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("Finding all customers is giving 0 records")
    void finding_all_customers_is_giving_0_records() {
        // GIVEN
        when(customerRepository.findAll()).thenReturn(Arrays.asList());

        // WHEN
        Set<Customer> result = customerService.findAll();

        // THEN
        assertEquals(new HashSet<>(Arrays.asList()), result);
        assertEquals(0, result.size());
    }

}