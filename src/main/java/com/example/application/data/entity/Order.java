package com.example.application.data.entity;

import org.semanticweb.owlapi.model.IRI;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order {

    private Product product;
    private Customer customer;
    private LocalDateTime datetime;
    private UUID orderNumber;

    public Order(Product product, Customer customer) {

        this.product = product;
        this.customer = customer;
        this.datetime = LocalDateTime.now();
        this.orderNumber = generateOrderNumber();
    }
    public Order()
    {

    }
    public Order(Product product, Customer customer, String datetime,UUID orderNumber) {
        this.product = product;
        this.customer = customer;
        this.datetime = LocalDateTime.parse(datetime);
        this.orderNumber = orderNumber;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public UUID getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(UUID orderNumber) {
        this.orderNumber = orderNumber;
    }

    public UUID generateOrderNumber() {
        return UUID.randomUUID();
    }

    public IRI getIndividualIRI(String OntologyIRIString)
    {
        return IRI.create(OntologyIRIString+"Order_Number_"+ orderNumber.toString());
    }

}

