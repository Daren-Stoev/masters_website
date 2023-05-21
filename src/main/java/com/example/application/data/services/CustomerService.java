package com.example.application.data.services;

import com.example.application.data.entity.Customer;
import com.example.application.data.ontologies.CustomerOntology;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerService {

    private CustomerOntology customerOntology = new CustomerOntology();

    private List<Customer> products = new ArrayList<Customer>();

    public void addCustomerToOntology(Customer customer) {
        customerOntology.addCustomer(customer);
    }


    public boolean EmailAlreadyExists(Customer customer) {
       return customerOntology.isCustomerEmailExists(customer.getEmail());
    }
}
