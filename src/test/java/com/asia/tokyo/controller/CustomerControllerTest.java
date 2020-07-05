package com.asia.tokyo.controller;

import com.asia.tokyo.controller.model.CustomerDto;
import com.asia.tokyo.domain.Customer;
import com.asia.tokyo.exception.CustomerException;
import com.asia.tokyo.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@WebMvcTest(CustomerController.class)
@DisplayName("Test CustomerController")
public class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerService customerService;

    private List<CustomerDto> customersDto;

    @BeforeEach
    public void setUp() {
        CustomerDto customer1 = CustomerDto.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        CustomerDto customer2 = CustomerDto.builder().id(UUID.randomUUID()).customerName("Marc Lee").tableNumber("2").build();
        CustomerDto customer3 = CustomerDto.builder().id(UUID.randomUUID()).customerName("Anna Smith").tableNumber("5").build();
        CustomerDto customer4 = CustomerDto.builder().id(UUID.randomUUID()).customerName("James-Lee Dog").tableNumber("8").build();

        customersDto = Arrays.asList(customer1, customer2, customer3, customer4);
    }

    @Test
    @DisplayName("Adding a new customer is responding status 200")
    public void adding_new_customer_is_responding_status_201() throws Exception {
        // GIVEN
        ConstrainedFields fields = new ConstrainedFields(Customer.class);
        CustomerDto customer = CustomerDto.builder().customerName("James Bond").tableNumber("10").build();
        CustomerDto customerAdded = CustomerDto.builder().id(UUID.randomUUID()).customerName("James Bond").tableNumber("10").build();
        given(customerService.addCustomer(any(CustomerDto.class))).willReturn(customerAdded);

        // WHEN THEN
        mvc.perform(post("/api/customer/add")
                .content(Utils.asJsonString(customer))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andDo(document("api/customer-new",
                requestFields(
                        fields.withPath("id").ignored(),
                        fields.withPath("version").ignored(),
                        fields.withPath("createdDate").ignored(),
                        fields.withPath("lastModifiedDate").ignored(),
                        fields.withPath("customerName").description("Name of the customer"),
                        fields.withPath("tableNumber").description("Number of the table")
                )));
    }

    @Test
    @DisplayName("Adding wrong data format for customer is responding status 400")
    public void adding_empty_customer_is_responding_status_400() throws Exception {
        // GIVEN WHEN THEN
        ConstrainedFields fields = new ConstrainedFields(Customer.class);
        mvc.perform(post("/api/customer/add")
                .content(Utils.asJsonString(Customer.builder().build()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Adding empty customer is responding status 400")
    public void adding_missing_required_fields_customer_is_responding_status_400() throws Exception {
        // GIVEN
        ConstrainedFields fields = new ConstrainedFields(Customer.class);
        CustomerDto customer = CustomerDto.builder().build();
        given(customerService.addCustomer(any(CustomerDto.class))).willThrow(new CustomerException("The customer informations were not provided."));

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
        ConstrainedFields fields = new ConstrainedFields(Customer.class);
        UUID uuid = UUID.randomUUID();
        CustomerDto customer = CustomerDto.builder().id(uuid).customerName("James Bond").tableNumber("10").build();
        given(customerService.findCustomerById(uuid)).willReturn(customer);

        // WHEN THEN
        mvc.perform(get("/api/customer/get/{uuid}", uuid.toString())
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("api/customer-get",
                        pathParameters(
                                parameterWithName("uuid").description("UUID for the customer to get.")),
                        responseFields(
                                fields.withPath("id").description("Id of the customer"),
                                fields.withPath("version").description("Version number"),
                                fields.withPath("createdDate").description("Date created"),
                                fields.withPath("lastModifiedDate").description("Date updated"),
                                fields.withPath("customerName").description("Name of the customer"),
                                fields.withPath("tableNumber").description("Number of the table")
                        )))
                .equals(customer);
    }

    @Test
    @DisplayName("Finding unknown uuid is giving a status 400")
    public void finding_unknown_customer_by_uuid_is_responding_status_400() throws Exception {
        // GIVEN
        ConstrainedFields fields = new ConstrainedFields(Customer.class);
        given(customerService.findCustomerById(any(UUID.class))).willThrow(new CustomerException("The uuid is unknown."));

        // WHEN THEN
        mvc.perform(get("/api/customer/get/{uuid}", UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Updating unknown customer is responding status 400")
    public void updating_unknown_customer_is_responding_status_400() throws Exception {
        // GIVEN
        ConstrainedFields fields = new ConstrainedFields(Customer.class);
        CustomerDto customer = CustomerDto.builder().customerName("James Bond").tableNumber("10").build();
        given(customerService.updateCustomer(any(CustomerDto.class))).willThrow(new CustomerException("This customer is unknown."));

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
        CustomerDto customer = CustomerDto.builder().customerName("James Bond").tableNumber("10").build();
        CustomerDto customerUpdated = CustomerDto.builder().id(uuid).customerName("James Bond").tableNumber("8").build();
        given(customerService.updateCustomer(any(CustomerDto.class))).willReturn(customerUpdated);

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
        ConstrainedFields fields = new ConstrainedFields(Customer.class);
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

        Set<CustomerDto> customers = new HashSet<>(customersDto);

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

        List<CustomerDto> customers = customersDto;
        List<CustomerDto> filteredCustomer = customers.stream().filter(c -> c.getCustomerName().contains(customerNamePattern)).collect(Collectors.toList());

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

        List<CustomerDto> customers = customersDto;
        List<CustomerDto> filteredCustomer = customers.stream().filter(c -> c.getCustomerName().contains(customerNamePattern)).collect(Collectors.toList());

        given(customerService.findAllByCustomerNameLike(any(String.class))).willReturn(filteredCustomer);

        // WHEN THEN
        mvc.perform(get("/api/customer/all/{patternName}", customerNamePattern)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}
