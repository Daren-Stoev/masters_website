package com.example.application.data.agents;

import com.example.application.data.entity.Customer;
import com.example.application.data.ontologies.CustomerOntology;
import com.example.application.security.AuthService;
import com.example.application.views.login.LoginView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ClientAgent extends Agent {

    private String customerEmail;
   // private final AgentContainer agentContainer;

    public ClientAgent() {
        //this.agentContainer = agentContainer;
    }
    private LoginView loginView;
    private CustomerOntology customerOntology;
    private AuthService authService;

    private AID customerAID;
    @Override
    public void setup() {
        customerOntology = new CustomerOntology();
        authService = new AuthService(customerOntology);
        loginView = new LoginView(authService,this);

        addBehaviour(new TickerBehaviour(this,2000) {
            @Override
            protected void onTick() {

                if(customerEmail != null)
                {
                    System.out.println("Trying to find a customer with email " + customerEmail);
                    DFAgentDescription dfd = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();

                    sd.setType("customer");

                    dfd.addServices(sd);

                    try {
                        DFAgentDescription[] descriptions =
                                DFService.search(myAgent, dfd);
                        customerAID = descriptions[0].getName();


                    } catch (FIPAException e) {
                        e.printStackTrace();
                    }
                    if(customerAID != null){
                        myAgent.addBehaviour(new FetchClientBehaviour ());
                    }
                    else {
                        System.out.println("No CustomerAID");
                    }
                }

            }
        });

    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }



    private class FetchClientBehaviour extends Behaviour {

        int step = 0;

        MessageTemplate messageTemplate;

        int repliesCount = 0;

        Customer customer;
        @Override
        public void action() {

            switch (step) {
                case 0:
                    System.out.println("Fetch me a client with email " + customerEmail);
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

                    cfp.addReceiver(customerAID);

                    cfp.setContent(customerEmail);
                    cfp.setConversationId("customer fetching");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis());

                    messageTemplate = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("customer fetching"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    send(cfp);

                    step++;

                    break;

                case 1:

                    ACLMessage reply = receive(messageTemplate);

                    if(reply != null) {

                        if(reply.getPerformative() == ACLMessage.PROPOSE)
                        {
                            ObjectMapper mapper = new ObjectMapper();

                            try {
                                customer  = mapper.readValue(
                                        reply.getContent(), Customer.class);

                                System.out.println("Client Agent Found customer");
                                customer.printInfo();
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }

                        }

                        repliesCount++;

                        if(repliesCount >= 1 ) {

                            step++;
                        }

                    }
                    break;

            }

        }

        @Override
        public boolean done() {
            if (step == 2) {

                if (customer == null){
                    System.out.println("Customer is null in done");
                }
                customerEmail = null;

                removeBehaviour(this);
                return true;
            }
            return false;
        }
    }
}
