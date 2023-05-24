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
        OWLClass productToRemove = dataFactory.getOWLClass(product.getIndividualIRI(ontologyIRIStr));

        OWLEntityRemover remover = new OWLEntityRemover(ontologyManager, Collections.singleton(ontology));

        productToRemove.accept(remover);

        ontologyManager.applyChanges(remover.getChanges());

        saveOntology();

    }

    public Product getProductById(String id) {
        OWLClass productClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Product"));
        OWLDataProperty idProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductId"));
        OWLDataProperty nameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductName"));
        OWLDataProperty descriptionProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductDescription"));
        OWLDataProperty priceProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductPrice"));
        OWLDataProperty imageProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductImage"));
        OWLDataProperty ownerProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductOwner"));
        OWLDataProperty categoryProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductCategory"));
        OWLDataProperty quantityProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductQuantity"));
        OWLIndividual individualId = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + id));



        OWLDataPropertyAssertionAxiom idAxiom = dataFactory.getOWLDataPropertyAssertionAxiom(idProperty, individualId, dataFactory.getOWLLiteral(id));

        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
        OWLNamedIndividual productIndividual = null;


        for (OWLNamedIndividual individual : individuals) {
            Set<OWLIndividualAxiom> axioms = ontology.getAxioms(individual);
            if (axioms.contains(idAxiom)) {
                Set<OWLClassExpression> types = individual.getTypes(ontology);
                if (types.contains(productClass)) {
                    String productId = retrieveDataPropertyValue(individual, idProperty);
                    String productName = retrieveDataPropertyValue(individual, nameProperty);
                    String description = retrieveDataPropertyValue(individual, descriptionProperty);
                    Double price =Double.parseDouble(retrieveDataPropertyValue(individual, priceProperty));
                    String imageUrl = retrieveDataPropertyValue(individual, imageProperty);
                    String owner_email = retrieveDataPropertyValue(individual, ownerProperty);

                    String category = retrieveDataPropertyValue(individual, categoryProperty);
                    Integer quantity = Integer.parseInt(retrieveDataPropertyValue(individual, quantityProperty));


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
    public Product getProductFromIndividual(OWLNamedIndividual productIndividual) {
        OWLDataProperty idProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductId"));
        OWLDataProperty nameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductName"));
        OWLDataProperty descriptionProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductDescription"));
        OWLDataProperty priceProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductPrice"));
        OWLDataProperty imageProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductImage"));
        OWLDataProperty ownerProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductOwner"));
        OWLDataProperty categoryProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductCategory"));
        OWLDataProperty quantityProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductQuantity"));
        // Add more properties as needed

        String productId = retrieveDataPropertyValue(productIndividual, idProperty);
        String productName = retrieveDataPropertyValue(productIndividual, nameProperty);
        String description = retrieveDataPropertyValue(productIndividual, descriptionProperty);
        Double price =Double.parseDouble(retrieveDataPropertyValue(productIndividual, priceProperty));
        String imageUrl = retrieveDataPropertyValue(productIndividual, imageProperty);
        String owner_email = retrieveDataPropertyValue(productIndividual, ownerProperty);
        String category = retrieveDataPropertyValue(productIndividual, categoryProperty);
        Integer quantity = Integer.parseInt(retrieveDataPropertyValue(productIndividual, quantityProperty));
        // Retrieve customer object property values
        Customer owner = customerOntology.getCustomer(owner_email);
        // Create Customer object and add it to the list
        Product product = new Product(productId,productName,description,price,imageUrl,owner,category,quantity);
        return product;
    }

    public ArrayList<Product> getAllProducts() {
        OWLClass productClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Product"));
        OWLDataProperty idProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductId"));
        OWLDataProperty nameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductName"));
        OWLDataProperty descriptionProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductDescription"));
        OWLDataProperty priceProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductPrice"));
        OWLDataProperty imageProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductImage"));
        OWLDataProperty ownerProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductOwner"));
        OWLDataProperty categoryProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductCategory"));
        OWLDataProperty quantityProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductQuantity"));

        ArrayList<Product> products = new ArrayList<>();

        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
        for (OWLNamedIndividual individual : individuals) {
            Set<OWLClassExpression> types = individual.getTypes(ontology);
            if (types.contains(productClass)) {
                // Retrieve product data from individual
                String productId = retrieveDataPropertyValue(individual, idProperty);
                String productName = retrieveDataPropertyValue(individual, nameProperty);
                String description = retrieveDataPropertyValue(individual, descriptionProperty);
                Double price =Double.parseDouble(retrieveDataPropertyValue(individual, priceProperty));
                String imageUrl = retrieveDataPropertyValue(individual, imageProperty);
                String owner_email = retrieveDataPropertyValue(individual, ownerProperty);

                String category = retrieveDataPropertyValue(individual, categoryProperty);
                Integer quantity = Integer.parseInt(retrieveDataPropertyValue(individual, quantityProperty));


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

    public void updateProductOntology(Product product) {
        // Get the existing individual for the product
        IRI productIRI = product.getIndividualIRI(ontologyIRIStr);
        OWLIndividual productIndividual = dataFactory.getOWLNamedIndividual(productIRI);

        // Get the properties of the product
        OWLDataProperty nameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductName"));
        OWLDataProperty descriptionProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductDescription"));
        OWLDataProperty priceProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductPrice"));
        OWLDataProperty imageProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductImage"));
        OWLDataProperty categoryProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "ProductCategory"));

        // Update the product's properties in the ontology
        OWLAxiom productName = dataFactory.getOWLDataPropertyAssertionAxiom(nameProperty, productIndividual, dataFactory.getOWLLiteral(product.getName()));
        OWLAxiom productDescription = dataFactory.getOWLDataPropertyAssertionAxiom(descriptionProperty, productIndividual, dataFactory.getOWLLiteral(product.getDescription()));
        OWLAxiom productPrice = dataFactory.getOWLDataPropertyAssertionAxiom(priceProperty, productIndividual, dataFactory.getOWLLiteral(product.getPrice()));
        OWLAxiom productImage = dataFactory.getOWLDataPropertyAssertionAxiom(imageProperty, productIndividual, dataFactory.getOWLLiteral(product.getImageUrl()));
        OWLAxiom productCategory = dataFactory.getOWLDataPropertyAssertionAxiom(categoryProperty, productIndividual, dataFactory.getOWLLiteral(product.getCategory()));

        // Remove the old axioms for the properties (optional)
        ontologyManager.removeAxioms(ontology, ontology.getAxioms(productIndividual));

        // Add the updated axioms to the ontology
        ontologyManager.addAxiom(ontology, productName);
        ontologyManager.addAxiom(ontology, productDescription);
        ontologyManager.addAxiom(ontology, productPrice);
        ontologyManager.addAxiom(ontology, productImage);
        ontologyManager.addAxiom(ontology, productCategory);

        // Save the ontology
        saveOntology();
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
