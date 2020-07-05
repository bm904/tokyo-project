package com.asia.tokyo.controller.mapper;

import com.asia.tokyo.controller.model.CustomerDto;
import com.asia.tokyo.domain.Customer;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {

    CustomerDto customerToCustomerDto(Customer customer);

    Customer customerDtoToCustomer(CustomerDto dto);

    List<Customer> customersDtoToCustomersList(List<CustomerDto> customerDtos);

    List<CustomerDto> customersToCustomerDtosList(List<Customer> customers);

    Set<Customer> customersDtoToCustomersSet(Set<CustomerDto> customerDtos);

    Set<CustomerDto> customersToCustomerDtosSet(Set<Customer> customers);
}
