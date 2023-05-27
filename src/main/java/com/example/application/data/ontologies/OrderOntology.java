package com.example.application.data.ontologies;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.ImageUtils;
import com.example.application.data.entity.Order;
import com.example.application.data.entity.Product;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class OrderOntology {

    private OWLOntology ontology;
    private OWLDataFactory dataFactory;
    private OWLOntologyManager ontologyManager;
    private OWLReasoner reasoner;
    private String ontologyIRIStr;
    private String ontologyFilePath = "src/files/test_owl.owx";

    private CustomerOntology customerOntology = new CustomerOntology();
    private ProductOntology productOntology = new ProductOntology();

    public OrderOntology() {
        // Load the ontology file
        ontologyManager = OWLManager.createOWLOntologyManager();
        loadOntologyFromFile();
        dataFactory = ontologyManager.getOWLDataFactory();

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);
        ontologyIRIStr = ontology.getOntologyID()
                .getOntologyIRI().toString() + "#";
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
    public void AddOrder(Order order) {
        OWLClass orderClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Order"));
        OWLDataProperty idProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "OrderNumber"));
        OWLDataProperty timestampProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "OrderDate"));

        OWLIndividual orderIndividual = dataFactory.getOWLNamedIndividual(order.getIndividualIRI(ontologyIRIStr));

        addCustomer(orderIndividual,order.getCustomer().getEmail());
        addProduct(orderIndividual,order.getProduct().getId());

        OWLAxiom classIsOrder = dataFactory.getOWLClassAssertionAxiom(orderClass,orderIndividual);

        OWLAxiom orderNumber = dataFactory.getOWLDataPropertyAssertionAxiom(idProperty,orderIndividual,dataFactory.getOWLLiteral(order.getOrderNumber().toString()));
        OWLAxiom orderTimestamp = dataFactory.getOWLDataPropertyAssertionAxiom(timestampProperty,orderIndividual,dataFactory.getOWLLiteral(order.getDatetime().toString()));

        ontologyManager.addAxiom(ontology,classIsOrder);
        ontologyManager.addAxiom(ontology,orderNumber);
        ontologyManager.addAxiom(ontology,orderTimestamp);

        OWLAxiom idUniqueness = dataFactory.getOWLFunctionalDataPropertyAxiom(idProperty);
        ontologyManager.addAxiom(ontology, idUniqueness);
        saveOntology();

    }
    public void addCustomer(OWLIndividual orderIndividual, String orderCustomer) {
        OWLClass customerClass = dataFactory.getOWLClass(
                IRI.create(ontologyIRIStr + orderCustomer));

        OWLObjectProperty hasOrderCustomer = dataFactory.
                getOWLObjectProperty(IRI.create(
                        ontologyIRIStr + "hasOrderCustomer"));
        reasoner.flush();
        // Create an instance of the subscription class
        OWLNamedIndividual customerInstance = dataFactory.getOWLNamedIndividual(
                IRI.create(ontologyIRIStr + orderCustomer));

        // Create an axiom stating that the subscription instance is of the subscription class
        OWLClassAssertionAxiom classAssertionAxiom = dataFactory.getOWLClassAssertionAxiom(customerClass, customerInstance);

        // Create an OWLObjectPropertyAssertionAxiom linking the customer and the subscription instance
        OWLObjectPropertyAssertionAxiom propertyAssertionAxiom = dataFactory.getOWLObjectPropertyAssertionAxiom(hasOrderCustomer, orderIndividual, customerInstance);

        // Add the axioms to the ontology
        AddAxiom addClassAssertionAxiom = new AddAxiom(ontology, classAssertionAxiom);
        AddAxiom addPropertyAssertionAxiom = new AddAxiom(ontology, propertyAssertionAxiom);

        //ontologyManager.applyChange(addClassAssertionAxiom);
        ontologyManager.applyChange(addPropertyAssertionAxiom);

        // Print the axioms added to the ontology
        System.out.println("Added class assertion axiom: " + classAssertionAxiom);
        System.out.println("Added property assertion axiom: " + propertyAssertionAxiom);
    }

    public void addProduct(OWLIndividual orderIndividual, String orderProduct){
        OWLClass productClass = dataFactory.getOWLClass(
                IRI.create(ontologyIRIStr + orderProduct));

        OWLObjectProperty hasOrderProduct = dataFactory.
                getOWLObjectProperty(IRI.create(
                        ontologyIRIStr + "hasOrderProduct"));
        reasoner.flush();
        // Create an instance of the subscription class
        OWLNamedIndividual productInstance = dataFactory.getOWLNamedIndividual(
                IRI.create(ontologyIRIStr + orderProduct));

        // Create an axiom stating that the subscription instance is of the subscription class
        OWLClassAssertionAxiom classAssertionAxiom = dataFactory.getOWLClassAssertionAxiom(productClass, productInstance);

        // Create an OWLObjectPropertyAssertionAxiom linking the customer and the subscription instance
        OWLObjectPropertyAssertionAxiom propertyAssertionAxiom = dataFactory.getOWLObjectPropertyAssertionAxiom(hasOrderProduct, orderIndividual, productInstance);

        // Add the axioms to the ontology
        AddAxiom addClassAssertionAxiom = new AddAxiom(ontology, classAssertionAxiom);
        AddAxiom addPropertyAssertionAxiom = new AddAxiom(ontology, propertyAssertionAxiom);

        //ontologyManager.applyChange(addClassAssertionAxiom);
        ontologyManager.applyChange(addPropertyAssertionAxiom);

        // Print the axioms added to the ontology
        System.out.println("Added class assertion axiom: " + classAssertionAxiom);
        System.out.println("Added property assertion axiom: " + propertyAssertionAxiom);

    }

    public ArrayList<Order> getAllOrders() {
        reasoner.flush();
        OWLClass orderClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Order"));
        OWLDataProperty idProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "OrderNumber"));
        OWLDataProperty timestampProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "OrderDate"));

        ArrayList<Order> orders = new ArrayList<>();
        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
        for (OWLNamedIndividual individual : individuals) {
            Set<OWLClassExpression> types = individual.getTypes(ontology);
            if (types.contains(orderClass)) {
                // Retrieve product data from individual
                String orderId = retrieveDataPropertyValue(individual, idProperty);
                String orderTimeStamp = retrieveDataPropertyValue(individual, timestampProperty);
                Customer customer = retrieveOrderCustomer(individual);
                Product product = retrieveOrderProduct(individual);

                // Create an Order object and add it to the list
                Order order = new Order(product,customer,orderTimeStamp, UUID.fromString(orderId));
                orders.add(order);
            }
        }
        return orders;
    }
    private Customer retrieveOrderCustomer(OWLNamedIndividual orderIndividual) {
        OWLObjectProperty hasOrderCustomer = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasOrderCustomer"));
        Set<OWLNamedIndividual> customerIndividuals = reasoner.getObjectPropertyValues(orderIndividual, hasOrderCustomer).getFlattened();
        if (!customerIndividuals.isEmpty()) {
            OWLNamedIndividual customerIndividual = (OWLNamedIndividual) customerIndividuals.iterator().next();
            return customerOntology.getCustomerFromIndividual(customerIndividual);
        }
        return null;
    }

    private Product retrieveOrderProduct(OWLNamedIndividual orderIndividual) {
        OWLObjectProperty hasOrderProduct = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasOrderProduct"));
        //Try with only ProductId and not productIndividual
        Set<OWLNamedIndividual> productIndividuals = reasoner.getObjectPropertyValues(orderIndividual, hasOrderProduct).getFlattened();
        if (!productIndividuals.isEmpty()) {
            OWLNamedIndividual productIndividual = (OWLNamedIndividual) productIndividuals.iterator().next();
            return productOntology.getProductFromIndividual(productIndividual);
        }
        return null;
    }
    private String retrieveDataPropertyValue(OWLNamedIndividual individual, OWLDataProperty property) {
        Set<OWLLiteral> literals = reasoner.getDataPropertyValues(individual, property);
        if (!literals.isEmpty()) {
            return literals.iterator().next().getLiteral();
        }
        return "";
    }

    public void removeOrder(Order order) {
        OWLClass orderToRemove = dataFactory.getOWLClass(order.getIndividualIRI(ontologyIRIStr));
        System.out.println("orderToRemove" + orderToRemove);
        OWLEntityRemover remover = new OWLEntityRemover(ontologyManager, Collections.singleton(ontology));

        orderToRemove.accept(remover);

        ontologyManager.applyChanges(remover.getChanges());

        saveOntology();
        reasoner.flush();
    }
}
