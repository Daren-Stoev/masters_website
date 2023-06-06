package com.example.application.data.ontologies;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Product;
import com.example.application.data.entity.ImageUtils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ProductOntology {
    private OWLOntology ontology;
    private OWLDataFactory dataFactory;
    private OWLOntologyManager ontologyManager;
    private OWLReasoner reasoner;
    private String ontologyIRIStr;
    private String ontologyFilePath = "src/files/test_owl.owx";

    private CustomerOntology customerOntology = new CustomerOntology();
    private ImageUtils imageUtils = new ImageUtils();

    public ProductOntology() {
        // Load the ontology file
        ontologyManager = OWLManager.createOWLOntologyManager();
        loadOntologyFromFile();
        dataFactory = ontologyManager.getOWLDataFactory();

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);
        ontologyIRIStr = ontology.getOntologyID()
                .getOntologyIRI().toString() + "#";
    }

    public boolean isProductIdExists(String id) {
        OWLDataProperty idProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductId"));

        // Iterate over individuals in the ontology to check if any has the same email
        for (OWLIndividual individual : ontology.getIndividualsInSignature()) {
            OWLDataPropertyAssertionAxiom idAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(idProperty, individual, dataFactory.getOWLLiteral(id));
            if (ontology.containsAxiom(idAssertion)) {
                return true; // Id already exists
            }
        }

        return false; // Id does not exist
    }

    private void loadOntologyFromFile() {
        File ontoFile = new File("src/files/test_owl.owx");

        try {
            ontology = ontologyManager
                    .loadOntologyFromOntologyDocument(ontoFile);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }
    private void saveOntology() {
        try {
            ontologyManager.saveOntology(ontology);
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    public void refreshOntology(){
        reasoner.flush();
    }

    public void addProduct(Product product) {

        OWLClass productClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Product"));
        OWLDataProperty idProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductId"));
        OWLDataProperty nameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductName"));
        OWLDataProperty descriptionProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductDescription"));
        OWLDataProperty priceProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductPrice"));
        OWLDataProperty imageProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductImage"));
        OWLDataProperty ownerProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductOwner"));
        OWLDataProperty categoryProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductCategory"));
        OWLDataProperty quantityProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductQuantity"));


        File imageFile = product.getImageFile();
        product.printValues();

        // Create an individual for the customer
        OWLIndividual productIndividual = dataFactory.getOWLNamedIndividual(product.getIndividualIRI(ontologyIRIStr));

        OWLAxiom classIsProduct = dataFactory.getOWLClassAssertionAxiom(productClass,productIndividual);

        OWLAxiom productId = dataFactory.getOWLDataPropertyAssertionAxiom(idProperty,productIndividual,dataFactory.getOWLLiteral(product.getId()));
        OWLAxiom productName = dataFactory.getOWLDataPropertyAssertionAxiom(nameProperty,productIndividual,dataFactory.getOWLLiteral(product.getName()));
        OWLAxiom productDescription = dataFactory.getOWLDataPropertyAssertionAxiom(descriptionProperty,productIndividual,dataFactory.getOWLLiteral(product.getDescription()));
        OWLAxiom productPrice = dataFactory.getOWLDataPropertyAssertionAxiom(priceProperty,productIndividual,dataFactory.getOWLLiteral(product.getPrice()));
        OWLAxiom productImage = dataFactory.getOWLDataPropertyAssertionAxiom(imageProperty,productIndividual,dataFactory.getOWLLiteral(product.getImageUrl()));
        OWLAxiom productOwner = dataFactory.getOWLDataPropertyAssertionAxiom(ownerProperty,productIndividual,dataFactory.getOWLLiteral(product.getOwner().getEmail()));
        OWLAxiom productCategory = dataFactory.getOWLDataPropertyAssertionAxiom(categoryProperty,productIndividual,dataFactory.getOWLLiteral(product.getCategory()));
        OWLAxiom productQuantity = dataFactory.getOWLDataPropertyAssertionAxiom(quantityProperty,productIndividual,dataFactory.getOWLLiteral(product.getQuantity()));



        ontologyManager.addAxiom(ontology, classIsProduct);
        ontologyManager.addAxiom(ontology, productId);
        ontologyManager.addAxiom(ontology, productName);
        ontologyManager.addAxiom(ontology, productDescription);
        ontologyManager.addAxiom(ontology, productPrice);
        ontologyManager.addAxiom(ontology, productImage);
        ontologyManager.addAxiom(ontology, productOwner);
        ontologyManager.addAxiom(ontology, productCategory);
        ontologyManager.addAxiom(ontology, productQuantity);


        OWLAxiom idUniqueness = dataFactory.getOWLFunctionalDataPropertyAxiom(idProperty);
        ontologyManager.addAxiom(ontology, idUniqueness);

        System.out.println("Product added successfully");

        // Save the ontology
        saveOntology();
    }


    public void removeProduct(Product product) {
        OWLNamedIndividual productToRemove = dataFactory.getOWLNamedIndividual(product.getIndividualIRI(ontologyIRIStr));
        OWLEntityRemover remover = new OWLEntityRemover(ontologyManager, Collections.singleton(ontology));

        // Visit the OWLIndividual representing the Order
        productToRemove.accept(remover);

        ontologyManager.applyChanges(remover.getChanges());

        saveOntology();
        reasoner.flush();

    }

    public Product getProductById(String id) {
        OWLClass productClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Product"));

        List<String> dataPropertyNames = Arrays.asList(
                "ProductId", "ProductName", "ProductDescription", "ProductPrice", "ProductImage", "ProductOwner",
                "ProductCategory", "ProductQuantity"
        );
        Map<String, OWLDataProperty> dataProperties = getDataProperties(dataPropertyNames);


        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();


        for (OWLNamedIndividual individual : individuals) {
            String individualId = retrieveDataPropertyValue(individual,  dataProperties.get("ProductId"));

            // Check if the email property value matches the given email
            if (id.equals(individualId))  {
                Set<OWLClassExpression> types = individual.getTypes(ontology);
                if (types.contains(productClass)) {
                    String productId = retrieveDataPropertyValue(individual, dataProperties.get("ProductId"));
                    String productName = retrieveDataPropertyValue(individual, dataProperties.get("ProductName"));
                    String description = retrieveDataPropertyValue(individual, dataProperties.get("ProductDescription"));
                    Double price = Double.parseDouble(retrieveDataPropertyValue(individual, dataProperties.get("ProductPrice")));
                    String imageUrl = retrieveDataPropertyValue(individual, dataProperties.get("ProductImage"));
                    String owner_email = retrieveDataPropertyValue(individual, dataProperties.get("ProductOwner"));
                    String category = retrieveDataPropertyValue(individual, dataProperties.get("ProductCategory"));
                    Integer quantity = Integer.parseInt(retrieveDataPropertyValue(individual, dataProperties.get("ProductQuantity")));


                    // Retrieve customer object property values
                    Customer owner = customerOntology.getCustomer(owner_email);
                    // Create Customer object and add it to the list
                    Product product = new Product(productId,productName,description,price,imageUrl,owner,category,quantity);
                    return product;
                }
            }
        }

        System.out.println("Product data not found for id: " + id);
        return null;
    }

    private Map<String, OWLDataProperty> getDataProperties(List<String> propertyNames) {
        Map<String, OWLDataProperty> dataProperties = new HashMap<>();
        for (String propertyName : propertyNames) {
            OWLDataProperty property = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + propertyName));
            dataProperties.put(propertyName, property);
        }
        return dataProperties;
    }
    public Product getProductFromIndividual(OWLNamedIndividual productIndividual) {
        List<String> dataPropertyNames = Arrays.asList(
                "ProductId", "ProductName", "ProductDescription", "ProductPrice", "ProductImage", "ProductOwner",
                "ProductCategory", "ProductQuantity"
        );
        Map<String, OWLDataProperty> dataProperties = getDataProperties(dataPropertyNames);
        // Add more properties as needed

        String productId = retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductId"));
        String productName = retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductName"));
        String description = retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductDescription"));
        Double price = Double.parseDouble(retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductPrice")));
        String imageUrl = retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductImage"));
        String owner_email = retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductOwner"));
        String category = retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductCategory"));
        Integer quantity = Integer.parseInt(retrieveDataPropertyValue(productIndividual, dataProperties.get("ProductQuantity")));
        // Retrieve customer object property values
        Customer owner = customerOntology.getCustomer(owner_email);
        // Create Customer object and add it to the list
        Product product = new Product(productId,productName,description,price,imageUrl,owner,category,quantity);
        return product;
    }

    public ArrayList<Product> getAllProducts() {
        reasoner.flush();
        OWLClass productClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Product"));
        List<String> dataPropertyNames = Arrays.asList(
                "ProductId", "ProductName", "ProductDescription", "ProductPrice", "ProductImage", "ProductOwner",
                "ProductCategory", "ProductQuantity"
        );
        Map<String, OWLDataProperty> dataProperties = getDataProperties(dataPropertyNames);
        // Add more properties as needed


        ArrayList<Product> products = new ArrayList<>();

        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
        for (OWLNamedIndividual individual : individuals) {
            Set<OWLClassExpression> types = individual.getTypes(ontology);
            if (types.contains(productClass)) {
                // Retrieve product data from individual
                String productId = retrieveDataPropertyValue(individual, dataProperties.get("ProductId"));
                String productName = retrieveDataPropertyValue(individual, dataProperties.get("ProductName"));
                String description = retrieveDataPropertyValue(individual, dataProperties.get("ProductDescription"));
                Double price = Double.parseDouble(retrieveDataPropertyValue(individual, dataProperties.get("ProductPrice")));
                String imageUrl = retrieveDataPropertyValue(individual, dataProperties.get("ProductImage"));
                String owner_email = retrieveDataPropertyValue(individual, dataProperties.get("ProductOwner"));
                String category = retrieveDataPropertyValue(individual, dataProperties.get("ProductCategory"));
                Integer quantity = 1;
                if ( dataProperties.get("ProductQuantity") != null &&  !dataProperties.get("ProductQuantity").toString().isEmpty()) {
                    quantity = Integer.parseInt(retrieveDataPropertyValue(individual, dataProperties.get("ProductQuantity")));;
                }


                // Retrieve customer object property values
                Customer owner = customerOntology.getCustomer(owner_email);
                // Create Customer object and add it to the list
                Product product = new Product(productId,productName,description,price,imageUrl,owner,category,quantity);
                products.add(product);
            }
        }

        return products;
    }

    private String retrieveDataPropertyValue(OWLNamedIndividual individual, OWLDataProperty property) {
        Set<OWLLiteral> literals = reasoner.getDataPropertyValues(individual, property);
        if (!literals.isEmpty()) {
            return literals.iterator().next().getLiteral();
        }
        return "";
    }

    public void updateProduct(Product product) {
        // Get the existing individual for the product
        IRI productIRI = product.getIndividualIRI(ontologyIRIStr);
        OWLIndividual productIndividual = dataFactory.getOWLNamedIndividual(productIRI);

        // Get the properties of the product
        OWLDataProperty nameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductName"));
        OWLDataProperty descriptionProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductDescription"));
        OWLDataProperty priceProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductPrice"));
        OWLDataProperty imageProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductImage"));
        OWLDataProperty categoryProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductCategory"));
        OWLDataProperty quantityProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductQuantity"));
        List<String> dataPropertyNames = Arrays.asList(
                "ProductName", "ProductDescription", "ProductPrice", "ProductImage", "ProductCategory", "ProductQuantity"
        );
        Map<String, OWLDataProperty> dataProperties = getDataProperties(dataPropertyNames);

        // Update the product's properties in the ontology
        OWLAxiom productName = dataFactory.getOWLDataPropertyAssertionAxiom(nameProperty, productIndividual, dataFactory.getOWLLiteral(product.getName()));
        OWLAxiom productDescription = dataFactory.getOWLDataPropertyAssertionAxiom(descriptionProperty, productIndividual, dataFactory.getOWLLiteral(product.getDescription()));
        OWLAxiom productPrice = dataFactory.getOWLDataPropertyAssertionAxiom(priceProperty, productIndividual, dataFactory.getOWLLiteral(product.getPrice()));
        OWLAxiom productImage = dataFactory.getOWLDataPropertyAssertionAxiom(imageProperty, productIndividual, dataFactory.getOWLLiteral(product.getImageUrl()));
        OWLAxiom productCategory = dataFactory.getOWLDataPropertyAssertionAxiom(categoryProperty, productIndividual, dataFactory.getOWLLiteral(product.getCategory()));
        OWLAxiom productQuantity = dataFactory.getOWLDataPropertyAssertionAxiom(quantityProperty, productIndividual, dataFactory.getOWLLiteral(product.getQuantity()));

        Set<OWLAxiom> filteredAxioms = ontology.getDataPropertyAssertionAxioms(productIndividual).stream()
                .filter(axiom -> axiom.getProperty().equals(nameProperty) ||
                        axiom.getProperty().equals(descriptionProperty) ||
                        axiom.getProperty().equals(priceProperty) ||
                        axiom.getProperty().equals(imageProperty) ||
                        axiom.getProperty().equals(categoryProperty) ||
                        axiom.getProperty().equals(quantityProperty))
                .collect(Collectors.toSet());

        ontologyManager.removeAxioms(ontology, filteredAxioms);
        // Add the updated axioms to the ontology
        ontologyManager.addAxiom(ontology, productName);
        ontologyManager.addAxiom(ontology, productDescription);
        ontologyManager.addAxiom(ontology, productPrice);
        ontologyManager.addAxiom(ontology, productImage);
        ontologyManager.addAxiom(ontology, productCategory);
        ontologyManager.addAxiom(ontology, productQuantity);

        // Save the ontology
        saveOntology();
        reasoner.flush();
    }




    public class ProductData<T> {
        public final  Map<OWLDataProperty,OWLLiteral> productDataPropertyValues;
        public final  Map<OWLObjectProperty,OWLIndividual> productObjectPropertyValues;
        public ProductData(Map<OWLDataProperty,OWLLiteral> productDataPropertyValues, Map<OWLObjectProperty,OWLIndividual> productObjectPropertyValues) {
            this.productObjectPropertyValues = productObjectPropertyValues;
            this.productDataPropertyValues = productDataPropertyValues;
        }
    }
    private String saveImageToFile(File imageFile) {

        String imageUrl = imageUtils.saveImageToFile(imageFile);

        return imageUrl;
    }
}
