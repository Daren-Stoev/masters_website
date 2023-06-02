package com.example.application.data.services;

import com.example.application.data.entity.Customer;
import com.example.application.data.ontologies.CustomerOntology;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerService {

    private CustomerOntology customerOntology;

    private ProductService productService;

    public CustomerService(){
        customerOntology = new CustomerOntology();
        productService = new ProductService();
    }

    private List<Customer> customers = new ArrayList<Customer>();

    public void addCustomerToOntology(Customer customer) {
        customerOntology.addCustomer(customer);
    }


    public boolean EmailAlreadyExists(Customer customer) {
       return customerOntology.isCustomerEmailExists(customer.getEmail());
    }
    public void updateCustomer(Customer customer) {
        customerOntology.updateCustomer(customer);
    }
    public void deleteCustomer(Customer customer) {
        productService.deleteCustomerByCustomer(customer);
        customerOntology.removeCustomer(customer);
    }
    public Customer getCustomerByEmail(String email) {
        Customer customer = customerOntology.getCustomer(email);
        return customer;
    }
}
