package com.trunghieu21132291.customerservice.services;

import com.trunghieu21132291.customerservice.entities.Customer;

public interface CustomerService {
    Customer save(Customer customer);

    Customer findById(long id);
}
