package com.example.application.data.ontologies;

import com.example.application.data.entity.Customer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomerOntology {
    private OWLOntology ontology;
    private OWLDataFactory dataFactory;
    private OWLOntologyManager ontologyManager;
    private OWLReasoner reasoner;
    private String ontologyIRIStr;
    private String ontologyFilePath = "src/files/test_owl.owx";

    public CustomerOntology() {
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

    // Function to add a customer to the ontology
    public void addCustomer(Customer customer) {


        OWLClass customerClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "User"));
        OWLDataProperty username = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Username"));
        OWLDataProperty email = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Email"));
        OWLDataProperty password = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Password"));
        OWLDataProperty password_salt = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "PasswordSalt"));
        OWLDataProperty firstName = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "FirstName"));
        OWLDataProperty lastName = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "LastName"));
        OWLDataProperty subscriptionType = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "SubscriptionType"));

        // Check if the customer email already exists in the ontology
        if (isCustomerEmailExists(customer.getEmail())) {
            System.out.println("Customer with this email already exists");
            return;
        }


        // Create an individual for the customer
        OWLIndividual customerIndividual = dataFactory.getOWLNamedIndividual(customer.getIndividualIRI(ontologyIRIStr));

        OWLAxiom classIsCustomer = dataFactory.getOWLClassAssertionAxiom(customerClass,customerIndividual);
        OWLAxiom customerUsername = dataFactory.getOWLDataPropertyAssertionAxiom(username,customerIndividual,dataFactory.getOWLLiteral(customer.getUsername()));
        OWLAxiom customerEmail = dataFactory.getOWLDataPropertyAssertionAxiom(email,customerIndividual,dataFactory.getOWLLiteral(customer.getEmail()));
        OWLAxiom customerPassword = dataFactory.getOWLDataPropertyAssertionAxiom(password,customerIndividual,dataFactory.getOWLLiteral(customer.getPassword()));
        OWLAxiom customerPassword_salt = dataFactory.getOWLDataPropertyAssertionAxiom(password_salt,customerIndividual,dataFactory.getOWLLiteral(customer.getPassword_saltAsString()));
        OWLAxiom customerFirstName = dataFactory.getOWLDataPropertyAssertionAxiom(firstName,customerIndividual,dataFactory.getOWLLiteral(customer.getFirstName()));
        OWLAxiom customerLastName = dataFactory.getOWLDataPropertyAssertionAxiom(lastName,customerIndividual,dataFactory.getOWLLiteral(customer.getLastName()));
        OWLAxiom customerSubscriptionType = dataFactory.getOWLDataPropertyAssertionAxiom(subscriptionType,customerIndividual,dataFactory.getOWLLiteral(customer.getSubscriptionType()));

        ontologyManager.addAxiom(ontology, classIsCustomer);
        ontologyManager.addAxiom(ontology, customerUsername);
        ontologyManager.addAxiom(ontology, customerEmail);
        ontologyManager.addAxiom(ontology, customerPassword);
        ontologyManager.addAxiom(ontology, customerPassword_salt);
        ontologyManager.addAxiom(ontology, customerFirstName);
        ontologyManager.addAxiom(ontology, customerLastName);
        ontologyManager.addAxiom(ontology, customerSubscriptionType);

        addSubscriptionType(customerIndividual,customer.getSubscriptionType());

        // Save the ontology
        saveOntology();
        reasoner.flush();
    }

    public boolean isCustomerEmailExists(String email) {
        OWLDataProperty emailProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Email"));

        // Iterate over individuals in the ontology to check if any has the same email
        for (OWLIndividual individual : ontology.getIndividualsInSignature()) {
            OWLDataPropertyAssertionAxiom emailAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(emailProperty, individual, dataFactory.getOWLLiteral(email));
            if (ontology.containsAxiom(emailAssertion)) {
                return true; // Email already exists
            }
        }

        return false; // Email does not exist
    }

    public void addSubscriptionType(OWLIndividual customerIndividual, String customerSubscriptionType) {
        OWLClass subscriptionClass = dataFactory.getOWLClass(
                IRI.create(ontologyIRIStr + customerSubscriptionType));

        OWLObjectProperty hasSubscription = dataFactory.
                getOWLObjectProperty(IRI.create(
                        ontologyIRIStr + "hasSubscription"));
        reasoner.flush();
        // Create an instance of the subscription class
        OWLNamedIndividual subscriptionInstance = dataFactory.getOWLNamedIndividual(
                IRI.create(ontologyIRIStr + customerSubscriptionType));

        // Create an axiom stating that the subscription instance is of the subscription class
        OWLClassAssertionAxiom classAssertionAxiom = dataFactory.getOWLClassAssertionAxiom(subscriptionClass, subscriptionInstance);

        // Create an OWLObjectPropertyAssertionAxiom linking the customer and the subscription instance
        OWLObjectPropertyAssertionAxiom propertyAssertionAxiom = dataFactory.getOWLObjectPropertyAssertionAxiom(hasSubscription, customerIndividual, subscriptionInstance);

        // Add the axioms to the ontology
        AddAxiom addClassAssertionAxiom = new AddAxiom(ontology, classAssertionAxiom);
        AddAxiom addPropertyAssertionAxiom = new AddAxiom(ontology, propertyAssertionAxiom);

        //ontologyManager.applyChange(addClassAssertionAxiom);
        ontologyManager.applyChange(addPropertyAssertionAxiom);

        // Print the axioms added to the ontology
        System.out.println("Added class assertion axiom: " + classAssertionAxiom);
        System.out.println("Added property assertion axiom: " + propertyAssertionAxiom);
    }

    public String getCustomerSubscriptionType(Customer customer) {
        IRI custoerIndividualIRI = customer.getIndividualIRI(ontologyIRIStr);
        OWLNamedIndividual customerIndividual = dataFactory.getOWLNamedIndividual(custoerIndividualIRI);
        OWLObjectProperty hasSubscriptionTypeProperty = dataFactory.getOWLObjectProperty(IRI.create( ontologyIRIStr + "hasSubscription"));

        for (OWLObjectPropertyAssertionAxiom assertion : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (assertion.getProperty().equals(hasSubscriptionTypeProperty) && assertion.getSubject().equals(customerIndividual)) {
                OWLIndividual object = assertion.getObject();
                if (object instanceof OWLNamedIndividual) {
                    OWLNamedIndividual subscriptionTypeIndividual = (OWLNamedIndividual) object;
                    return subscriptionTypeIndividual.getIRI().getFragment();  // Return the subscription type name
                }
            }
        }

        return null;  // Subscription type not found
    }

    public void updateCustomer(Customer customer) {
        IRI customerIRI = customer.getIndividualIRI(ontologyIRIStr);
        OWLIndividual customerIndividual = dataFactory.getOWLNamedIndividual(customerIRI);

        // Get the properties of the product
        OWLDataProperty username = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Username"));
        OWLDataProperty email = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Email"));
        OWLDataProperty password = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Password"));
        OWLDataProperty firstName = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "FirstName"));
        OWLDataProperty lastName = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "LastName"));
        OWLDataProperty subscriptionType = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "SubscriptionType"));

        OWLAxiom customerUsername = dataFactory.getOWLDataPropertyAssertionAxiom(username,customerIndividual,dataFactory.getOWLLiteral(customer.getUsername()));
        OWLAxiom customerEmail = dataFactory.getOWLDataPropertyAssertionAxiom(email,customerIndividual,dataFactory.getOWLLiteral(customer.getEmail()));
        OWLAxiom customerPassword = dataFactory.getOWLDataPropertyAssertionAxiom(password,customerIndividual,dataFactory.getOWLLiteral(customer.getPassword()));
        OWLAxiom customerFirstName = dataFactory.getOWLDataPropertyAssertionAxiom(firstName,customerIndividual,dataFactory.getOWLLiteral(customer.getFirstName()));
        OWLAxiom customerLastName = dataFactory.getOWLDataPropertyAssertionAxiom(lastName,customerIndividual,dataFactory.getOWLLiteral(customer.getLastName()));
        OWLAxiom customerSubscriptionType = dataFactory.getOWLDataPropertyAssertionAxiom(subscriptionType,customerIndividual,dataFactory.getOWLLiteral(customer.getSubscriptionType()));

        Set<OWLAxiom> filteredAxioms = ontology.getDataPropertyAssertionAxioms(customerIndividual).stream()
                .filter(axiom -> axiom.getProperty().equals(username)
                        || axiom.getProperty().equals(password)
                        || axiom.getProperty().equals(email)
                        || axiom.getProperty().equals(firstName)
                        || axiom.getProperty().equals(lastName)
                        || axiom.getProperty().equals(subscriptionType))
                .collect(Collectors.toSet());

        ontologyManager.removeAxioms(ontology, filteredAxioms);

        ontologyManager.addAxiom(ontology, customerUsername);
        ontologyManager.addAxiom(ontology, customerEmail);
        ontologyManager.addAxiom(ontology, customerPassword);
        ontologyManager.addAxiom(ontology, customerFirstName);
        ontologyManager.addAxiom(ontology, customerLastName);
        ontologyManager.addAxiom(ontology, customerSubscriptionType);

        // Save the ontology
        saveOntology();
        reasoner.flush();


    }

    // Function to remove a customer from the ontology
    public void removeCustomer(Customer customer) {
        OWLNamedIndividual customerToRemove = dataFactory.getOWLNamedIndividual(customer.getIndividualIRI(ontologyIRIStr));
        OWLEntityRemover remover = new OWLEntityRemover(ontologyManager, Collections.singleton(ontology));

        // Visit the OWLIndividual representing the Order
        customerToRemove.accept(remover);

        ontologyManager.applyChanges(remover.getChanges());

        saveOntology();
        reasoner.flush();

    }

    public Customer getCustomer(String email) {
        reasoner.flush();
        OWLClass customerClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "User"));
        OWLDataProperty emailProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Email"));
        OWLDataProperty usernameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Username"));
        OWLDataProperty passwordProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Password"));
        OWLDataProperty passwordSaltProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "PasswordSalt"));
        OWLDataProperty firstNameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "FirstName"));
        OWLDataProperty lastNameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "LastName"));
        OWLObjectProperty subscriptionTypeProperty = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasSubscription"));

        //System.out.println("Email: " + email);
        //System.out.println("Email Property: " + emailProperty);

        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
        OWLNamedIndividual customerIndividual = null;
        for (OWLNamedIndividual individual : individuals) {
            Set<OWLIndividualAxiom> axioms = ontology.getAxioms(individual);
            String individualEmail = retrieveDataPropertyValue(individual, emailProperty);

            // Check if the email property value matches the given email
            if (email.equals(individualEmail))  {

                customerIndividual = individual;
                Set<OWLClassExpression> types = individual.getTypes(ontology);
                if (types.contains(customerClass)) {

                    String username = retrieveDataPropertyValue(individual, usernameProperty);
                    String password = retrieveDataPropertyValue(individual, passwordProperty);
                    String firstName = retrieveDataPropertyValue(individual, firstNameProperty);
                    String lastName = retrieveDataPropertyValue(individual, lastNameProperty);
                    String passwordSalt = retrieveDataPropertyValue(individual,passwordSaltProperty);

                    // Retrieve customer object property values
                    OWLNamedIndividual subscriptionTypeIndividual = (OWLNamedIndividual) retrieveObjectPropertyValue(individual, subscriptionTypeProperty);
                    String subscriptionType = subscriptionTypeIndividual != null ? subscriptionTypeIndividual.getIRI().getFragment() : "";
                    // Create Customer object and add it to the list
                    Customer customer = new Customer(username, password,passwordSalt, email, subscriptionType, firstName, lastName);

                    return customer;
                }
                /*
                CustomerData cData = new CustomerData(customerDataPropertyValues, customerObjectPropertyValues);

                System.out.println("Found customer data:");
                System.out.println("Email: " + customerDataPropertyValues.get(emailProperty).getLiteral());
                for (Map.Entry<OWLDataProperty, OWLLiteral> entry : customerDataPropertyValues.entrySet()) {
                    OWLDataProperty property = entry.getKey();
                    OWLLiteral value = entry.getValue();

                    System.out.println(property.getIRI().getFragment() + ": " + value.getLiteral());
                }

                return cData;

                 */
            }
        }

        System.out.println("Customer data not found for email: " + email);
        return null;
    }

    public ArrayList<Customer> getAllCustomers() {
        OWLClass customerClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "User"));
        OWLDataProperty emailProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Email"));
        OWLDataProperty usernameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "UserName"));
        OWLDataProperty passwordProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Password"));
        OWLDataProperty passwordSaltProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "PasswordSalt"));
        OWLDataProperty firstNameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "FirstName"));
        OWLDataProperty lastNameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "LastName"));
        OWLObjectProperty subscriptionTypeProperty = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasSubscription"));

        ArrayList<Customer> customers = new ArrayList<>();

        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
        for (OWLNamedIndividual individual : individuals) {
            Set<OWLClassExpression> types = individual.getTypes(ontology);
            if (types.contains(customerClass)) {
                // Retrieve customer data properties
                String username = retrieveDataPropertyValue(individual,usernameProperty);
                String email = retrieveDataPropertyValue(individual, emailProperty);
                String password = retrieveDataPropertyValue(individual, passwordProperty);
                String firstName = retrieveDataPropertyValue(individual, firstNameProperty);
                String lastName = retrieveDataPropertyValue(individual, lastNameProperty);
                String passwordSalt = retrieveDataPropertyValue(individual,passwordSaltProperty);

                // Retrieve customer object property values
                OWLNamedIndividual subscriptionTypeIndividual =(OWLNamedIndividual) retrieveObjectPropertyValue(individual, subscriptionTypeProperty);
                String subscriptionType = subscriptionTypeIndividual != null ? subscriptionTypeIndividual.getIRI().getFragment() : "";

                // Create Customer object and add it to the list
                Customer customer = new Customer(username, password,passwordSalt,email,subscriptionType, firstName, lastName);
                customers.add(customer);
            }
        }

        return customers;
    }

    public Customer getCustomerFromIndividual(OWLNamedIndividual customerIndividual)
    {
        OWLDataProperty emailProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Email"));
        OWLDataProperty usernameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Username"));
        OWLDataProperty passwordProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "Password"));
        OWLDataProperty passwordSaltProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "PasswordSalt"));
        OWLDataProperty firstNameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "FirstName"));
        OWLDataProperty lastNameProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "LastName"));
        OWLObjectProperty subscriptionTypeProperty = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasSubscription"));

        String email = retrieveDataPropertyValue(customerIndividual, emailProperty);
        String username = retrieveDataPropertyValue(customerIndividual, usernameProperty);
        String password = retrieveDataPropertyValue(customerIndividual, passwordProperty);
        String firstName = retrieveDataPropertyValue(customerIndividual, firstNameProperty);
        String lastName = retrieveDataPropertyValue(customerIndividual, lastNameProperty);
        String passwordSalt = retrieveDataPropertyValue(customerIndividual,passwordSaltProperty);
        OWLNamedIndividual subscriptionType = (OWLNamedIndividual) retrieveObjectPropertyValue(customerIndividual, subscriptionTypeProperty);
        String subscriptionTypeString = subscriptionType.getIRI().getFragment();
        return new Customer(username, password,passwordSalt, email, subscriptionTypeString, firstName, lastName);

    }

    private String retrieveDataPropertyValue(OWLNamedIndividual individual, OWLDataProperty property) {
        Set<OWLLiteral> literals = reasoner.getDataPropertyValues(individual, property);
        if (!literals.isEmpty()) {
            return literals.iterator().next().getLiteral();
        }
        return "";
    }

    private OWLIndividual retrieveObjectPropertyValue(OWLNamedIndividual individual, OWLObjectProperty property) {
        Set<OWLNamedIndividual> values = reasoner.getObjectPropertyValues(individual, property).getFlattened();
        if (!values.isEmpty()) {
            return values.iterator().next();
        }
        return null;
    }


    // Utility function to save the ontology
    private void saveOntology() {
        try {
            ontologyManager.saveOntology(ontology);
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }



    // Other functions for retrieving and updating customer information from the ontology can be added here
}
