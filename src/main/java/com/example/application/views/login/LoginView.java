package com.example.application.views.login;

import com.example.application.security.AuthService;
import com.example.application.views.itemlist.ItemListView;
import com.example.application.views.signup.SignUpView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("login")
@PageTitle("Login | Vaadin CRM")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final AuthService authService;
    public LoginView(AuthService authService){
        this.authService = authService;
        setId("login-view");
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");
        Button loginButton = new Button("Login");
        loginButton.addClickListener(e -> {
            try {
                authService.authenticate(emailField.getValue(),passwordField.getValue());
                Notification.show("Hello ");
                UI.getCurrent().navigate(ItemListView.class);
            } catch (AuthService.AuthException ex) {
                System.out.println(ex);
                Notification.show(emailField.getValue() + " | " + passwordField.getValue());
            }
        });


        Button signUpButton = new Button("Sign up");
        signUpButton.addClickListener(e -> {
            UI.getCurrent().navigate(SignUpView.class);
        });
        add(new H1("Login"),emailField,passwordField,loginButton,signUpButton);
    }
}