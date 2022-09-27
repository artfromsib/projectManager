package com.ym.projectManager.service;

import com.ym.projectManager.model.Customer;


import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    Customer createOrUpdateCustomer(Customer customer);

    Customer getCustomer(long id);

    List<Customer> getCustomersByName(String name);
}
