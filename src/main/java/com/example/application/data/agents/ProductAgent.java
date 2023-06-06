package com.example.application.data.agents;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Product;
import com.example.application.data.ontologies.ProductOntology;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductAgent extends Agent {

    private ProductOntology productOntology;

    static ProductAgent instance = null;

    private List<Product> products = new ArrayList<Product>();

    public ProductAgent(){

    }
    public static void setInstance(ProductAgent instance) throws Exception {
        ProductAgent.instance = instance;
        System.out.println(instance);
    }

    public static ProductAgent getInstance() {
        if(instance == null) {
            instance = new ProductAgent();
        }
        return instance;
    }

    @Override
    protected void setup(){

        productOntology = new ProductOntology();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("product");
        sd.setName("product agent");

        dfd.addServices(sd);

        try {
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        addBehaviour(new FetchProductBehaviour());
        addBehaviour(new CRUDProductBehaviour());
    }



    private class FetchProductBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.
                    MatchPerformative(ACLMessage.CFP);
            ACLMessage message = receive(messageTemplate);

            if (message != null) {
                System.out.println(message);
                System.out.println("ProductAgent Message is not null");
                String messageJson = message.getContent();
                System.out.println(messageJson);
                ObjectMapper mapper = new ObjectMapper();
                String messageId = message.getConversationId();
                if (messageId != null) {
                    switch (messageId) {
                        case "get all products":
                            products = getAllProducts();
                            sendReply(products,message);
                            break;
                        case "get product by id":
                            String productId = message.getContent();
                            Product product = getProductById(productId);
                            ArrayList<Product> singleProductArray = new ArrayList<Product>();
                            singleProductArray.add(product);
                            sendReply(singleProductArray,message,true);

                            break;

                        case "get products by customer":
                            try {
                                Customer customer = mapper.readValue(messageJson, Customer.class);
                                products = getProductsByCustomer(customer);
                                sendReply(products,message);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "get product by name":
                            String name = message.getContent();
                            products = findByNameStartsWithIgnoreCase(name);
                            sendReply(products,message);

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

        public void sendReply(List<Product> products, ACLMessage message) {
            sendReply(products, message, false);
        }
        public void sendReply(List<Product> products,ACLMessage message,Boolean single){
            ACLMessage reply = message.createReply();
            ObjectMapper mapper = new ObjectMapper();
            if (!single) {
                if (products != null && products.size() > 0) {
                    System.out.println("Products size : " + products.size());


                    reply.setPerformative(ACLMessage.PROPOSE);


                    try {
                        reply.setContent(mapper.writeValueAsString(products));
                        reply.setLanguage("JSON");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("failed");
                    System.out.println("FAILED TO FETCH PRODUCTS");
                }

                send(reply);
            } else {
                Product product = products.get(0);
                if (product != null) {
                    System.out.println("Found Product by Id");


                    reply.setPerformative(ACLMessage.PROPOSE);


                    try {
                        reply.setContent(mapper.writeValueAsString(product));
                        reply.setLanguage("JSON");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                }  else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("failed");
                    System.out.println("FAILED TO FETCH PRODUCT");
                }
                send(reply);

            }

        }
        public List<Product> findByNameStartsWithIgnoreCase(String value) {

            products = productOntology.getAllProducts();

            //filter by name
            List<Product> filteredProducts = filterByNameContainsIgnoreCase(products, value);

            return filteredProducts;
        }
        public List<Product> filterByNameContainsIgnoreCase(List<Product> products,String value) {

            //filter by name
            List<Product> filteredProducts = new ArrayList<Product>();

            for (Product product : products) {
                if (product.getName().toLowerCase().startsWith(value.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }

            return filteredProducts;
        }

        public List<Product> getAllProducts() {
            return productOntology.getAllProducts();
        }

        public Product getProductById(String id) {
            Product product = productOntology.getProductById(id);
            return product;
        }

    }




    private class CRUDProductBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = receive(messageTemplate);
            if (message != null) {
                System.out.println(message);
                System.out.println("ProductAgent CRUD Message is not null");
                String productJson = message.getContent();
                System.out.println(productJson);
                ObjectMapper mapper = new ObjectMapper();
                String messageId = message.getConversationId();
                if (messageId != null) {
                    switch (messageId) {
                        case "add product":

                            try {
                                Product product = mapper.readValue(productJson, Product.class);
                                System.out.println("Customer info before saving to ontology");
                                product.printValues();
                                addProductToOntology(product);
                                productOntology.refreshOntology();

                                ACLMessage reply = message.createReply();
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("Product added to ontology");
                                send(reply);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "update product":
                            try {
                                Product product = mapper.readValue(productJson, Product.class);
                                updateProduct(product);
                                productOntology.refreshOntology();

                                ACLMessage reply = message.createReply();
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("Product updated successfully");
                                send(reply);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case "delete product":
                            try {
                                Product product = mapper.readValue(productJson, Product.class);
                                deleteProduct(product);
                                productOntology.refreshOntology();

                                ACLMessage reply = message.createReply();
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("Product deleted successfully");
                                send(reply);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case "delete product by customer":
                            try {
                                Customer customer = mapper.readValue(productJson, Customer.class);
                                deleteProductByCustomer(customer);
                                productOntology.refreshOntology();

                                ACLMessage reply = message.createReply();
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("Products by customer deleted successfully");
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

        public void addProductToOntology(Product product) {
            product.printValues();
            System.out.println(productOntology);
            try {
                productOntology.addProduct(product);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        public void deleteProduct(Product product) {
            // Add cascade delete here like in service
            productOntology.removeProduct(product);
        }

        public void updateProduct(Product product) {
            productOntology.updateProduct(product);
        }

        public void deleteProductByCustomer(Customer customer) {
            List<Product> products = getProductsByCustomer(customer);
            System.out.println("Deleting " + products.size() + " products");
            for (Product product : products) {
                deleteProduct(product);
            }
        }

    }




    //Not optimized at all but it works,maybe
    public List<Product> getProductsByCustomer(Customer customer) {
        List<Product> allProducts = productOntology.getAllProducts();
        List<Product> result = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getOwner().getEmail().equals(customer.getEmail())) {
                result.add(product);
            }
        }
        return result;

    }

}

