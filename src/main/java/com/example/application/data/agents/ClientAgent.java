package com.example.application.data.agents;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Product;
import com.example.application.data.ontologies.CustomerOntology;
import com.example.application.security.AuthService;
import com.example.application.views.login.LoginView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ClientAgent extends Agent {
    public interface CustomerCallback {
        void onCustomerRetrieved(Customer customer);
    }
    public interface ProductCallback {
        void onProductRetrieved(Product product);
    }

    public interface ProductListCallback {
        void onProductListRetrieved(List<Product> productList);
    }

    private String customerEmail;

    static ClientAgent instance = null;

    public ClientAgent() {
    }

    private CustomerOntology customerOntology;
    private AuthService authService;

    public static void setInstance(ClientAgent instance) throws Exception {
        ClientAgent.instance = instance;
        System.out.println(instance);
    }

    public static ClientAgent getInstance() {
        if (instance == null) {
            instance = new ClientAgent();
        }
        System.out.println(instance);
        return instance;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public void setup() {
        customerOntology = new CustomerOntology();
    }

    public void addCustomerToOntology(Customer customer) {
        CRUDCustomer(customer,"add customer");
    }
    public void updateCustomer(Customer customer) {
        CRUDCustomer(customer,"update customer");
    }
    public void deleteCustomer(Customer customer) {
        CRUDCustomer(customer,"delete customer");
    }

    public void addProductToOntology(Product product) {
        CRUDProduct(product,"add product");
    }
    public void updateProduct(Product product) {
        CRUDProduct(product,"update product");
    }
    public void deleteProduct(Product product) {
        CRUDProduct(product,"delete product");
    }

    public void deleteAllCustomerProducts(Customer customer) {
        DeleteProductsByCustomerClass(customer,"delete product by customer");
    }

    public void getCustomerFromOntology(String customerEmail, CustomerCallback callback) {
        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            @Override
            public void action() {
                if (customerEmail != null) {
                    System.out.println("Trying to find a customer with email " + customerEmail);
                    DFAgentDescription dfd = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("customer");
                    dfd.addServices(sd);

                    try {
                        DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                        customerAID = descriptions[0].getName();
                    } catch (FIPAException e) {
                        e.printStackTrace();
                    }

                    if (customerAID != null) {
                        myAgent.addBehaviour(new FetchClientBehaviour(callback,customerAID));
                    } else {
                        System.out.println("No CustomerAID");
                    }
                }
            }
        });
    }


    private void CRUDCustomer(Customer customer,String action) {

        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            @Override
            public void action() {

                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("customer");
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                    customerAID = descriptions[0].getName();
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.addReceiver(customerAID);


                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String customerJson = mapper.writeValueAsString(customer);
                    message.setContent(customerJson);
                    message.setLanguage("JSON");
                    message.setOntology("customer-ontology"); // Set the ontology name
                    message.setConversationId(action);
                    System.out.println("CLIENTAGENT MESSAGE");
                    System.out.println(message.getContent());
                    System.out.println(message);
                    send(message);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CRUDProduct(Product product,String action) {

        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            @Override
            public void action() {

                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("product");
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                    customerAID = descriptions[0].getName();
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.addReceiver(customerAID);


                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String productJson = mapper.writeValueAsString(product);
                    message.setContent(productJson);
                    message.setLanguage("JSON");
                    message.setOntology("product-ontology"); // Set the ontology name
                    message.setConversationId(action);
                    System.out.println("CLIENTAGENT MESSAGE");
                    System.out.println(message.getContent());
                    System.out.println(message);
                    send(message);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void DeleteProductsByCustomerClass(Customer customer,String action) {

        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            @Override
            public void action() {

                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("product");
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                    customerAID = descriptions[0].getName();
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.addReceiver(customerAID);


                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String customerJson = mapper.writeValueAsString(customer);
                    message.setContent(customerJson);
                    message.setLanguage("JSON");
                    message.setOntology("product-ontology"); // Set the ontology name
                    message.setConversationId(action);
                    System.out.println("CLIENTAGENT MESSAGE");
                    System.out.println(message.getContent());
                    System.out.println(message);
                    send(message);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getProductFromOntology(String productId, ProductCallback callback,Runnable completionCallback) {
        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            String action = "get product by id";
            @Override
            public void action() {
                if (productId != null) {
                    System.out.println("Trying to find a product with id " + productId);
                    DFAgentDescription dfd = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("product");
                    dfd.addServices(sd);

                    try {
                        DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                        customerAID = descriptions[0].getName();
                    } catch (FIPAException e) {
                        e.printStackTrace();
                    }

                    if (customerAID != null) {
                        myAgent.addBehaviour(new FetchProductByIdBehaviour(callback,completionCallback,productId,customerAID,action));
                    } else {
                        System.out.println("No CustomerAID");
                    }
                }
            }
        });
    }
    public void getAllProductsFromOntology(ProductListCallback callback,Runnable completionCallback) {
        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            String action = "get all products";
            @Override
            public void action() {
                    System.out.println("Trying to fetch all products");
                    DFAgentDescription dfd = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("product");
                    dfd.addServices(sd);

                    try {
                        DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                        customerAID = descriptions[0].getName();
                    } catch (FIPAException e) {
                        e.printStackTrace();
                    }

                    if (customerAID != null) {
                        myAgent.addBehaviour(new FetchAllProductsBehaviour(callback,completionCallback,customerAID,action));
                    } else {
                        System.out.println("No CustomerAID");
                    }
                }
        });

    }

    public void getProductStartWithNameFromOntology(String name,ProductListCallback callback,Runnable completionCallback) {
        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            String action = "get product by name";
            @Override
            public void action() {
                System.out.println("Trying to fetch all products");
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("product");
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                    customerAID = descriptions[0].getName();
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

                if (customerAID != null) {
                    myAgent.addBehaviour(new FetchProductsByNameBehaviour(callback,completionCallback,name,customerAID,action));
                } else {
                    System.out.println("No CustomerAID");
                }
            }
        });

    }
    public void getProductsByCustomerFromOntology(Customer customer,ProductListCallback callback,Runnable completionCallback) {
        addBehaviour(new OneShotBehaviour(this) {
            AID customerAID;
            String action = "get products by customer";
            @Override
            public void action() {
                System.out.println("Trying to fetch all products from customer " + customer.getEmail());
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("product");
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] descriptions = DFService.search(myAgent, dfd);
                    customerAID = descriptions[0].getName();
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

                if (customerAID != null) {
                    myAgent.addBehaviour(new FetchProductsByCustomerBehaviour(callback,completionCallback,customer,customerAID,action));
                } else {
                    System.out.println("No CustomerAID");
                }
            }
        });

    }


    private class FetchClientBehaviour extends OneShotBehaviour {
        private final CustomerCallback callback;
        private final AID customerAID;

        public FetchClientBehaviour(CustomerCallback callback,AID customerAID) {
            this.callback = callback;
            this.customerAID = customerAID;
        }

        @Override
        public void action() {
            System.out.println("Fetch me a client with email " + customerEmail);
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.addReceiver(customerAID);
            cfp.setContent(customerEmail);
            cfp.setConversationId("customer fetching");
            cfp.setReplyWith("cfp" + System.currentTimeMillis());
            MessageTemplate messageTemplate = MessageTemplate.and(
                    MessageTemplate.MatchConversationId("customer fetching"),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
            send(cfp);

            ACLMessage reply = blockingReceive(messageTemplate);
            if (reply != null && reply.getPerformative() == ACLMessage.PROPOSE) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Customer customer = mapper.readValue(reply.getContent(), Customer.class);
                    System.out.println("Client Agent Found customer");
                    customer.printInfo();

                    // Invoke the callback with the retrieved customer object
                    callback.onCustomerRetrieved(customer);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processProductListReply(ACLMessage reply, ProductListCallback callback,Runnable completionCallback) {
        if (reply != null && reply.getPerformative() == ACLMessage.PROPOSE) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<Product> products = mapper.readValue(reply.getContent(), new TypeReference<List<Product>>() {});
                System.out.println("Client Agent Found " + products.size() + " products");

                // Invoke the callback with the retrieved product list
                callback.onProductListRetrieved(products);
                completionCallback.run();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
    private class FetchProductByIdBehaviour extends OneShotBehaviour {
        private final ProductCallback callback;
        private final String productId;

        private final AID customerAID;
        private final String action;
        private final Runnable completionCallback;

        public FetchProductByIdBehaviour(ProductCallback callback,Runnable completionCallback,String productId,AID customerAID,String action) {

            this.callback = callback;
            this.productId = productId;
            this.customerAID = customerAID;
            this.action = action;
            this.completionCallback = completionCallback;
        }

        @Override
        public void action() {
            System.out.println("Fetch me a Product with id " + productId);
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.addReceiver(customerAID);
            cfp.setContent(productId);
            cfp.setConversationId(action);
            cfp.setReplyWith("cfp" + System.currentTimeMillis());
            MessageTemplate messageTemplate = MessageTemplate.and(
                    MessageTemplate.MatchConversationId(action),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
            send(cfp);

            ACLMessage reply = blockingReceive(messageTemplate);
            if (reply != null && reply.getPerformative() == ACLMessage.PROPOSE) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Product product = mapper.readValue(reply.getContent(), Product.class);
                    System.out.println("Client Agent Found product");
                    product.printValues();

                    // Invoke the callback with the retrieved customer object
                    callback.onProductRetrieved(product);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class FetchAllProductsBehaviour extends OneShotBehaviour{

        private final ProductListCallback callback;
        private final AID customerAID;
        private final String action;

        private Runnable completionCallback;

        public FetchAllProductsBehaviour(ProductListCallback callback,Runnable completionCallback,AID customerAID,String action) {

            this.callback = callback;
            this.customerAID = customerAID;
            this.action = action;
            this.completionCallback = completionCallback;
        }
        @Override
        public void action() {
            System.out.println("Fetch me All products");
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.addReceiver(customerAID);
            cfp.setConversationId(action);
            cfp.setReplyWith("cfp" + System.currentTimeMillis());
            MessageTemplate messageTemplate = MessageTemplate.and(
                    MessageTemplate.MatchConversationId(action),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
            send(cfp);

            ACLMessage reply = blockingReceive(messageTemplate);
            processProductListReply(reply, callback,completionCallback);
        }
    }

    private class FetchProductsByNameBehaviour extends OneShotBehaviour{

        private final ProductListCallback callback;
        private final AID customerAID;
        private final String action;

        private final String name;
        private final Runnable completionCallback;

        public FetchProductsByNameBehaviour(ProductListCallback callback,Runnable completionCallback,String name,AID customerAID,String action) {

            this.callback = callback;
            this.customerAID = customerAID;
            this.action = action;
            this.name = name;
            this.completionCallback = completionCallback;
        }
        @Override
        public void action() {
            System.out.println("Fetch me products that start with " + name);
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.addReceiver(customerAID);
            cfp.setContent(name);
            cfp.setConversationId(action);
            cfp.setReplyWith("cfp" + System.currentTimeMillis());
            MessageTemplate messageTemplate = MessageTemplate.and(
                    MessageTemplate.MatchConversationId(action),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
            send(cfp);

            ACLMessage reply = blockingReceive(messageTemplate);
            processProductListReply(reply, callback,completionCallback);
        }
    }
    private class FetchProductsByCustomerBehaviour extends OneShotBehaviour{

        private final ProductListCallback callback;
        private final AID customerAID;
        private final String action;

        private final Customer customer;

        private final Runnable completionCallback;

        public FetchProductsByCustomerBehaviour(ProductListCallback callback,Runnable completionCallback,Customer customer,AID customerAID,String action) {

            this.callback = callback;
            this.customerAID = customerAID;
            this.action = action;
            this.customer = customer;
            this.completionCallback = completionCallback;
        }
        @Override
        public void action() {
            System.out.println("Fetch me products owned by  " + customer.getEmail());
            ObjectMapper mapper = new ObjectMapper();
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.addReceiver(customerAID);
            try {
                String customerString = mapper.writeValueAsString(customer);
                cfp.setContent(customerString);
                cfp.setLanguage("JSON");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            cfp.setConversationId(action);
            cfp.setReplyWith("cfp" + System.currentTimeMillis());
            MessageTemplate messageTemplate = MessageTemplate.and(
                    MessageTemplate.MatchConversationId(action),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
            send(cfp);

            ACLMessage reply = blockingReceive(messageTemplate);
            processProductListReply(reply, callback,completionCallback);
        }
    }
}
