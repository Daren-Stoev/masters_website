package com.example.application.data.services;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Order;
import com.example.application.data.entity.Product;

import java.util.ArrayList;
import java.util.List;

import com.example.application.data.ontologies.OrderOntology;
import com.example.application.data.ontologies.ProductOntology;

public class ProductService {

    private ProductOntology productOntology;

    private OrderService orderService;

    public ProductService(){
        productOntology = new ProductOntology();
        orderService = new OrderService();
    }

    private List<Product> products = new ArrayList<Product>();

    public List<Product> findByNameStartsWithIgnoreCase(String value) {

        products = productOntology.getAllProducts();

        //filter by name
        List<Product> filteredProducts = filterByNameContainsIgnoreCase(products, value);

        return filteredProducts;
    }

    public List<Product> filterByNameContainsIgnoreCase(List<Product> products,String value) {

        //filter by name
        List<Product> filteredProducts = new ArrayList<Product>();

        for (Product product : products) {
            if (product.getName().toLowerCase().startsWith(value.toLowerCase())) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }
    public Product getProductById(String id) {
        return productOntology.getProductById(id);
    }
    public List<Product> getAllProducts() {
        return productOntology.getAllProducts();
    }

    public void addProductToOntology(Product product) {
        product.printValues();
        System.out.println(productOntology);
        try {
            productOntology.addProduct(product);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Not optimized at all but it works,maybe
    public List<Product> getProductsByCustomer(Customer customer) {
        List<Product> allProducts = productOntology.getAllProducts();
        List<Product> result = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getOwner().getEmail().equals(customer.getEmail())) {
                result.add(product);
            }
        }
    return result;

    }

    public void deleteProduct(Product product) {
        orderService.deleteOrderByProduct(product);
        productOntology.removeProduct(product);
    }

    public void updateProduct(Product product) {
        productOntology.updateProduct(product);
    }

    public void deleteCustomerByCustomer(Customer customer) {
        List<Product> products = getProductsByCustomer(customer);
        for (Product product : products) {
            deleteProduct(product);
        }
    }
}
