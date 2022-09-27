package com.ym.projectManager.service.impl;

import com.ym.projectManager.model.Customer;
import com.ym.projectManager.repository.CustomerRepository;
import com.ym.projectManager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer createOrUpdateCustomer(Customer customer){
        if (customer.getCustomerId() == null) {
            return customerRepository.save(customer);
        } else {
            return customerRepository.saveAndFlush(customer);
        }
    }

    @Override
    public Customer getCustomer(long id){
        return customerRepository.getById(id);
    }

    @Override
    public List<Customer> getCustomersByName(String name){
        return customerRepository.getCustomerByFullNameIsLike(name);
    }
}
