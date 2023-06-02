    package com.example.application.data.entity;

    import com.example.application.data.ontologies.ProductOntology;
    import org.semanticweb.owlapi.model.IRI;

    import java.io.File;
    import java.util.UUID;

    public class Product {
        private String id;
        private String name;
        private String description;
        private Double price;
        private File imageFile;
        private String imageUrl;
        private Customer owner;
        private String category;
        private Integer quantity;

        private ProductOntology productOntology = new ProductOntology();
        // Constructors, getters, and setters

        public Product(String id, String name, String description, double price, String imageUrl, Customer owner, String category,Integer quantity) {

            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.imageUrl = imageUrl;
            this.owner = owner;
            this.category = category;
            this.quantity = quantity;
        }

        public Product(String name, String description, double price, String category,Integer quantity) {
            String id = generateString();
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.quantity = quantity;
        }

        public Product(){
            String id = generateString();
            this.id = id;
        }
        public Product(String id)
        {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setId(String id) {
            System.out.println("Setting id");
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            System.out.println("Setting Name");
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            System.out.println("Setting Description");
            this.description = description;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            System.out.println("Setting Price");
            this.price = price;
        }

        public File getImageFile() {
            return imageFile;
        }

        public void setImageFile(File imageFile) {
            System.out.println("Setting Image File");
            this.imageFile = imageFile;
        }

        public Customer getOwner() {
            return owner;
        }

        public void setOwner(Customer owner) {
            System.out.println("Setting Owner");
            this.owner = owner;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            System.out.println("Setting Category");
            this.category = category;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            System.out.println("Setting Quantity");
            this.quantity = quantity;
        }
        public String getImageUrl() {
            return imageUrl;
        }
        public void setImageUrl(String imageUrl) {
            System.out.println("Setting Image Url");
            this.imageUrl = imageUrl;
        }

        public IRI getIndividualIRI(String ontologyIRIStr) {
            return IRI.create(ontologyIRIStr + getId());
        }


        public String generateString() {
            System.out.println("Generating UUID");
            String uuid = UUID.randomUUID().toString();
            return uuid;
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

        public void printValues() {
            System.out.println("Id: " + getId());
            System.out.println("Name: " + getName());
            System.out.println("Description: " + getDescription());
            System.out.println("Price: " + getPrice());
            System.out.println("Image URL: " + getImageUrl());
            System.out.println("Category: " + getCategory());
            System.out.println("Quantity: " + getQuantity());
        }
    }
