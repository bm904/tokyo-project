package com.asia.tokyo.repository;

import com.asia.tokyo.domain.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, UUID> {

    @Query("SELECT c FROM Customer c WHERE c.customerName like ?1")
    List<Customer> findAllByCustomerNameLike(String customerName);
}
