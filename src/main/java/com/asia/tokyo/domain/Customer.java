package com.asia.tokyo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Customer extends BaseEntity{
    @Builder
    public Customer(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerName, String tableNumber) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerName = customerName;
        this.tableNumber = tableNumber;
    }

    private String customerName;

    private String tableNumber;
}
