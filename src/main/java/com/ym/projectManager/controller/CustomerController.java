package com.ym.projectManager.controller;

import com.ym.projectManager.model.Customer;
import com.ym.projectManager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/main/customers")

public class CustomerController {
    private final CustomerService customerService;

    @GetMapping(value = "")
    public ResponseEntity<List<Customer>> getCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return responseCustomers(customers);
    }

    @GetMapping(value = "/name")
    public ResponseEntity<List<Customer>> findCustomersByName(@RequestParam(value = "name", required = false) String name) {
        List<Customer> customers = customerService.getCustomersByName(name);
        return responseCustomers(customers);
    }

    private ResponseEntity<List<Customer>> responseCustomers(List<Customer> customers) {
        return customers != null && customers.isEmpty()
                ? new ResponseEntity<>(customers, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value ="/{id}")
    public ResponseEntity<Customer> getCustomer(@RequestParam(value = "id", required = false) long id) {
        Customer customer = customerService.getCustomer(id);
        return customer != null
                ? new ResponseEntity<>(customer, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



}
