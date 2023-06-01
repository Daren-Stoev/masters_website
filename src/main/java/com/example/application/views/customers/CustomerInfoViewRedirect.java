package com.example.application.views.customers;

import com.example.application.data.entity.Customer;
import com.example.application.data.services.CustomerService;
import com.example.application.views.MainLayout;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "customer-info-redirect",layout = MainLayout.class)
public class CustomerInfoViewRedirect  extends VerticalLayout {
    private Customer customer;
    public CustomerInfoViewRedirect(){

        customer = VaadinSession.getCurrent().getAttribute(Customer.class);

        if (customer != null) {
            UI.getCurrent().navigate(CustomerInfoView.class, customer.getEmail());
        } else {
            UI.getCurrent().navigate(LoginView.class);
        }

    }
}
