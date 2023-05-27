package com.example.application.data.services;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Order;
import com.example.application.data.entity.Product;
import com.example.application.data.ontologies.OrderOntology;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private OrderOntology orderOntology;

    public OrderService() {
        orderOntology = new OrderOntology();
    }

    public void AddOrder(Order order) {
        orderOntology.AddOrder(order);
    }
    public ArrayList<Order> getAllOrders()
    {
        return orderOntology.getAllOrders();
    }
    public ArrayList<Order> getOrdersByCustomer(Customer customer){

        ArrayList<Order> orders = getAllOrders();

        //filter by name
        ArrayList<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            System.out.println("customer Order");
            order.printInfo();
            System.out.println(order.getCustomer().getEmail());
            System.out.println(customer.getEmail());
            if (order.getCustomer().getEmail().equals(customer.getEmail())) {
                System.out.println("Added to list");
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }
    public ArrayList<Order> getOrdersByProduct(Product product){
        ArrayList<Order> orders = getAllOrders();
        //filter by name
        ArrayList<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            order.printInfo();
            if (order.getProduct().getId().equals(product.getId())) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }

    public ArrayList<Order> getOrdersInTimeRange(LocalDateTime startTime, LocalDateTime endTime)
    {
        ArrayList<Order> orders = getAllOrders();

        //filter by name
        ArrayList<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            if (order.getDatetime().isAfter(startTime) && order.getDatetime().isBefore(endTime)) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }

    public ArrayList<Order> getOrdersByProductCategory(String category){
        ArrayList<Order> orders = getAllOrders();

        //filter by name
        ArrayList<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            if (order.getProduct().getCategory().equals(category)) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }
    public void removeOrder(Order order){
        orderOntology.removeOrder(order);
    }
}
