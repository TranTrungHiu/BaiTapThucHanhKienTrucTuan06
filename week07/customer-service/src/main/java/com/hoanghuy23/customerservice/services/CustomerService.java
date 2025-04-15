package com.hoanghuy23.customerservice.services;

import com.hoanghuy23.customerservice.entities.Customer;

public interface CustomerService {
    Customer save(Customer customer);
    Customer findById(long id);
}

