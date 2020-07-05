package com.asia.tokyo.controller;

import com.asia.tokyo.domain.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Integration Test CustomerController")
public class CustomerControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int randomServerPort;

    @Test
    @DisplayName("Adding a new customer is responding status 201")
    public void adding_new_customer_is_responding_status_201() throws URISyntaxException {
        //GIVEN
        Customer customer = Customer.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();

        String baseUrl = "http://localhost:"+randomServerPort+"/api/customer/add";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");
        HttpEntity<Customer> request = new HttpEntity<>(customer, headers);

        //WHEN
        ResponseEntity<Customer> result = testRestTemplate.postForEntity(uri, request, Customer.class);

        //THEN
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

}
