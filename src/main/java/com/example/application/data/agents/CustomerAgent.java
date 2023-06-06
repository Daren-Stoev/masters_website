package com.example.application.data.agents;

import com.example.application.data.entity.Customer;
import com.example.application.data.ontologies.CustomerOntology;
import com.example.application.security.AuthService;
import com.example.application.views.login.LoginView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerAgent extends Agent {

    private CustomerOntology customerOntology;

    static CustomerAgent instance = null;

    public CustomerAgent() {
    }
    public static void setInstance(CustomerAgent instance) throws Exception {
        CustomerAgent.instance = instance;
        System.out.println(instance);
    }

    public static CustomerAgent getInstance() {
        if (instance == null) {
            instance = new CustomerAgent();
        }
        System.out.println(instance);
        return instance;
    }

    @Override
    protected void setup(){

        customerOntology = new CustomerOntology();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("customer");
        sd1.setName("customer agent");

        dfd.addServices(sd1);


        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new FetchCustomerBehaviour());
        addBehaviour(new CRUDCustomerBehaviour());
    }

    private List<Customer> customers = new ArrayList<Customer>();


    private class FetchCustomerBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.
                    MatchPerformative(ACLMessage.CFP);
            ACLMessage message = receive(messageTemplate);

            if (message != null) {
                String customerEmail = message.getContent();
                System.out.println("Fetching customer from email " + customerEmail);
                ObjectMapper mapper = new ObjectMapper();
                ACLMessage reply = message.createReply();

                Customer customer = getCustomerByEmail(customerEmail);


                if (customer != null) {
                    System.out.println("Found Customer");
                    reply.setPerformative(ACLMessage.PROPOSE);


                    try {
                        reply.setContent(mapper.writeValueAsString(customer));
                        reply.setLanguage("JSON");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("failed");
                    System.out.println("FAILED TO FETCH CUSTOMER");
                }

                send(reply);
            }
        }
        private Customer getCustomerByEmail(String email) {
            Customer customer = customerOntology.getCustomer(email);
            return customer;
        }

    }
    private class CRUDCustomerBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = receive(messageTemplate);
            if (message != null) {
                System.out.println(message);
                System.out.println("CustomerAgent Message is not null");
                String customerJson = message.getContent();
                System.out.println(customerJson);
                ObjectMapper mapper = new ObjectMapper();
                String messageId = message.getConversationId();
                if (messageId != null) {
                    switch (messageId) {
                        case "add customer":

                            try {
                                Customer customer = mapper.readValue(customerJson, Customer.class);
                                if (!customerOntology.isCustomerEmailExists(customer.getEmail())) {
                                    System.out.println("Customer info before saving to ontology");
                                    customer.printInfo();
                                    addCustomerToOntology(customer);
                                    customerOntology.refreshOntology();

                                    ACLMessage reply = message.createReply();
                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setContent("Customer added to ontology");
                                    send(reply);
                                } else {
                                    ACLMessage reply = message.createReply();
                                    reply.setPerformative(ACLMessage.REFUSE);
                                    reply.setContent("failed");
                                    System.out.println("Email already in use");

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "update customer":
                            try {
                                Customer customer = mapper.readValue(customerJson, Customer.class);
                                updateCustomer(customer);
                                customerOntology.refreshOntology();

                                ACLMessage reply = message.createReply();
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("Customer updated successfully");
                                send(reply);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case "delete customer":
                            try {
                                Customer customer = mapper.readValue(customerJson, Customer.class);
                                deleteCustomer(customer);
                                customerOntology.refreshOntology();

                                ACLMessage reply = message.createReply();
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("Customer deleted successfully");
                                send(reply);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        default:
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("failed");
                            System.out.println("Something went wrong(Switch case)");
                            send(reply);
                            break;
                    }
                }
            }
        }


        private void addCustomerToOntology(Customer customer) {
            // Perform the operation to add the customer to the ontology
            customerOntology.addCustomer(customer);
        }
        private void updateCustomer(Customer customer) {
            customerOntology.updateCustomer(customer);
        }

        private void deleteCustomer(Customer customer) {
            //productService.deleteCustomerByCustomer(customer);
            customerOntology.removeCustomer(customer);
        }
    }
}
