package com.example.application.views.customers;


import com.example.application.data.agents.ClientAgent;
import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Order;
import com.example.application.data.entity.Product;
import com.example.application.data.services.CustomerService;
import com.example.application.data.services.OrderService;
import com.example.application.views.MainLayout;
import com.example.application.views.orders.OrderView;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

@Route(value = "customer-info",layout = MainLayout.class)
public class CustomerInfoView extends Main implements HasComponents, HasUrlParameter<String> {

    private Customer customer;
    
    private CustomerService customerService;;
    
    private OrderService  orderService;

    private ClientAgent clientAgent;
    
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter == null) {
            parameter = VaadinSession.getCurrent().getAttribute(Customer.class).getEmail();
        }
        clientAgent = ClientAgent.getInstance();
        customerService = new CustomerService();
        addClassNames("customer-info");
        customer = customerService.getCustomerByEmail(parameter);
        customer.printInfo();
        customerService = new CustomerService();
        orderService = new OrderService();

        VerticalLayout main = constructUI();
        add(main);
    }

    public VerticalLayout constructUI() {

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setVisible(true);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems((FlexComponent.Alignment.CENTER));
        H1 title = new H1(customer.getUsername());
        titleLayout.add(title);
        mainLayout.add(titleLayout);

        Div customerInformationDiv = new Div();
        Span firstName = new Span(customer.getFirstName());
        Span lastName = new Span(customer.getLastName());
        Span email = new Span(customer.getEmail());
        customerInformationDiv.add(firstName,lastName,email);
        mainLayout.add(customerInformationDiv);
        //Get products and orders for this customer
        List<Product> products = new ArrayList<>();
        clientAgent.getProductsByCustomerFromOntology(customer, products::addAll,
                () -> {
                    Span productsSpan = new Span("Products: " + products.size());
                    mainLayout.add(productsSpan);
                });
        List<Order> orders = orderService.getOrdersByCustomer(customer);
        //List<Order> orders = new ArrayList<>();
        //clientAgent.getOrdersByCustomerFromOntology(customer, orderList -> orders.addAll(orderList));


        Span ordersSpan = new Span("Orders: " + orders.size());
        Button ordersButton = new Button("See all Orders");
        ordersButton.addClickListener(e -> {
            UI.getCurrent().navigate(OrderView.class);
        });
        mainLayout.add(ordersSpan, ordersButton);

        Button editButton = new Button("Edit");
        editButton.addClickListener(e -> {
            UI.getCurrent().navigate(CustomerEdit.class,customer.getEmail());
        });


        if(customer.getEmail().equals(VaadinSession.getCurrent().getAttribute(Customer.class).getEmail())) {
            mainLayout.add(editButton);
        }
        return mainLayout;
    }
}
