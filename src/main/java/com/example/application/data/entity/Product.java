package com.example.application.data.entity;

import com.example.application.data.ontologies.ProductOntology;
import org.semanticweb.owlapi.model.IRI;

import java.io.File;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private File imageFile;
    private String imageUrl;
    private Customer owner;
    private String category;
    private Integer quantity;

    private ProductOntology productOntology = new ProductOntology();
    // Constructors, getters, and setters

    public Product(String id, String name, String description, double price, String imageUrl, Customer owner, String category,Integer quantity) {

        boolean idExists = productOntology.isProductIdExists(id);
        if (idExists) {
            throw new IllegalArgumentException("Product id must be unique.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.owner = owner;
        this.category = category;
        this.quantity = quantity;
    }

    public Product(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public IRI getIndividualIRI(String ontologyIRIStr) {
        return IRI.create(ontologyIRIStr + getId());
    }

    public void updateProduct(String name, String description, double price, File imageFile, String category) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price > 0) {
            this.price = price;
        }
        if (imageFile != null) {
            this.imageFile = imageFile;
        }
        if (category != null) {
            this.category = category;
        }
    }
}
