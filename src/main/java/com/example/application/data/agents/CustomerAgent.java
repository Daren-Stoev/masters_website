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
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class CustomerAgent extends Agent {

    private CustomerOntology customerOntology;

    @Override
    protected void setup(){

        customerOntology = new CustomerOntology();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("customer");
        sd.setName("customer agent");

        dfd.addServices(sd);

        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new FetchCustomerBehaviour());
    }

    private List<Customer> customers = new ArrayList<Customer>();

    public void addCustomerToOntology(Customer customer) {
        customerOntology.addCustomer(customer);
    }


    public boolean EmailAlreadyExists(Customer customer) {
        return customerOntology.isCustomerEmailExists(customer.getEmail());
    }
    public void updateCustomer(Customer customer) {
        customerOntology.updateCustomer(customer);
    }
    public void deleteCustomer(Customer customer) {
        //productService.deleteCustomerByCustomer(customer);
        customerOntology.removeCustomer(customer);
    }
    public Customer getCustomerByEmail(String email) {
        Customer customer = customerOntology.getCustomer(email);
        return customer;
    }

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
                        reply.setContent("JSON");
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

    }
}
