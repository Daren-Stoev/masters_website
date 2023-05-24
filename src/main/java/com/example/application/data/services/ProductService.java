package com.example.application.data.services;

import com.example.application.data.entity.Product;

import java.util.ArrayList;
import java.util.List;

import com.example.application.data.ontologies.ProductOntology;

public class ProductService {

    private ProductOntology productOntology = new ProductOntology();

    private List<Product> products = new ArrayList<Product>();

    public List<Product> findByNameStartsWithIgnoreCase(String value) {

        products = productOntology.getAllProducts();

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
}
