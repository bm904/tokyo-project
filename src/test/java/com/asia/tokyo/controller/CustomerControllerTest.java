package com.asia.tokyo.controller;

import com.asia.tokyo.domain.Customer;
import com.asia.tokyo.exception.CustomerException;
import com.asia.tokyo.service.CustomerService;
import com.asia.tokyo.service.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("Test CustomerController")
public class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("Adding a new customer is responding status 200")
    public void adding_new_customer_is_responding_status_201() throws Exception {
        // GIVEN
        Customer customer = Customer.builder().customerName("James Bond").tableNumber("10").build();
        Customer customerAdded = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        given(customerService.addCustomer(any(Customer.class))).willReturn(customerAdded);

        // WHEN THEN
        mvc.perform(post("/api/customer/add")
                .content(Utils.asJsonString(customer))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Adding wrong data format for customer is responding status 400")
    public void adding_empty_customer_is_responding_status_400() throws Exception {
        // GIVEN WHEN THEN
        mvc.perform(post("/api/customer/add")
                .content(Utils.asJsonString(null))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Adding empty customer is responding status 400")
    public void adding_missing_required_fields_customer_is_responding_status_400() throws Exception {
        // GIVEN
        Customer customer = Customer.builder().build();
        given(customerService.addCustomer(any(Customer.class))).willThrow(new CustomerException("The customer informations were not provided."));

        mvc.perform(post("/api/customer/add")
                .content(Utils.asJsonString(customer))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Finding customer by his uuid is giving status 200")
    public void finding_customer_by_uuid_is_responding_status_200() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        Customer customer = Customer.builder().id(uuid).customerName("James Bond").tableNumber("10").build();
        given(customerService.findCustomerById(uuid)).willReturn(customer);

        // WHEN THEN
        mvc.perform(get("/api/customer/get/"+uuid.toString())
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .equals(customer);
    }

    @Test
    @DisplayName("Finding unknown uuid is giving a status 400")
    public void finding_unknown_customer_by_uuid_is_responding_status_400() throws Exception {
        // GIVEN
        given(customerService.findCustomerById(any(UUID.class))).willThrow(new CustomerException("The uuid is unknown."));

        // WHEN THEN
        mvc.perform(get("/api/customer/get/{uuid}", UUID.randomUUID())
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Updating unknown customer is responding status 400")
    public void updating_unknown_customer_is_responding_status_400() throws Exception {
        // GIVEN
        Customer customer = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        given(customerService.updateCustomer(any(Customer.class))).willThrow(new CustomerException("This customer is unknown."));

        // WHEN THEN
        mvc.perform(put("/api/customer/update")
                .content(Utils.asJsonString(customer))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Updating known customer is responding status 200")
    public void updating_known_customer_is_responding_status_200() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        Customer customer = Customer.builder().id(uuid).customerName("James Bond").tableNumber("10").build();
        Customer customerUpdated = Customer.builder().id(uuid).customerName("James Bond").tableNumber("8").build();
        given(customerService.updateCustomer(any(Customer.class))).willReturn(customerUpdated);

        // WHEN THEN
        mvc.perform(put("/api/customer/update")
                .content(Utils.asJsonString(customer))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deleting a known customer is responding status 204")
    public void deleting_a_known_customer_is_responding_status_204() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        doNothing().when(customerService).deleteCustomer(uuid);

        // WHEN THEN
        mvc.perform(delete("/api/customer/delete/{uuid}", uuid))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deleting unknown customer is responding status 400")
    public void deleting_unkown_customer_is_responding_status_400() throws Exception {
        // GIVEN
        doThrow(new CustomerException("This UUIS is unknow.")).when(customerService).deleteCustomer(any(UUID.class));

        // WHEN THEN
        mvc.perform(delete("/api/customer/delete/{uuid}", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Displaying all customers is responding status 200")
    public void getting_all_customers_is_responding_status_200() throws Exception {
        // GIVEN
        Customer customer1 = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).customerName("Marc Lee").tableNumber("2").build();
        Customer customer3 = Customer.builder().id(UUID.randomUUID()).customerName("Anna Smith").tableNumber("5").build();
        Customer customer4 = Customer.builder().id(UUID.randomUUID()).customerName("James-Lee Dog").tableNumber("8").build();
        Set<Customer> customers = new HashSet<>(Arrays.asList(customer1, customer2, customer3, customer4));

        given(customerService.findAll()).willReturn(customers);

        // WHEN THEN
        mvc.perform(get("/api/customer/all")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @DisplayName("Displaying all customers by name pattern is responding 2 records and status 200")
    public void getting_all_customers_by_name_pattern__is_responding_2_records_and_status_200() throws Exception {
        // GIVEN
        String customerNamePattern = "James";
        Customer customer1 = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).customerName("Marc Lee").tableNumber("2").build();
        Customer customer3 = Customer.builder().id(UUID.randomUUID()).customerName("Anna Smith").tableNumber("5").build();
        Customer customer4 = Customer.builder().id(UUID.randomUUID()).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> customers = Arrays.asList(customer1, customer2, customer3, customer4);
        List<Customer> filteredCustomer = customers.stream().filter(c -> c.getCustomerName().contains(customerNamePattern)).collect(Collectors.toList());

        given(customerService.findAllByCustomerNameLike(any(String.class))).willReturn(filteredCustomer);

        // WHEN THEN
        mvc.perform(get("/api/customer/all/{patternName}", customerNamePattern)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Displaying all customers by name pattern is responding 0 records and status 200")
    public void getting_all_customers_by_name_pattern__is_responding_0_records_and_status_200() throws Exception {
        // GIVEN
        String customerNamePattern = "Lucy";
        Customer customer1 = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).customerName("Marc Lee").tableNumber("2").build();
        Customer customer3 = Customer.builder().id(UUID.randomUUID()).customerName("Anna Smith").tableNumber("5").build();
        Customer customer4 = Customer.builder().id(UUID.randomUUID()).customerName("James-Lee Dog").tableNumber("8").build();
        List<Customer> customers = Arrays.asList(customer1, customer2, customer3, customer4);
        List<Customer> filteredCustomer = customers.stream().filter(c -> c.getCustomerName().contains(customerNamePattern)).collect(Collectors.toList());

        given(customerService.findAllByCustomerNameLike(any(String.class))).willReturn(filteredCustomer);

        // WHEN THEN
        mvc.perform(get("/api/customer/all/{patternName}", customerNamePattern)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
