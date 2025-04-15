package customer.services;


import customer.entities.Customer;

public interface CustomerService {

    Customer save(Customer customer);

    Customer findById(long id);
}
