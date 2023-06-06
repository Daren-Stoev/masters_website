package com.example.application.views.customers;

import ch.qos.logback.core.subst.NodeToStringTransformer;
import com.example.application.data.agents.ClientAgent;
import com.example.application.data.agents.CustomerAgent;
import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Product;
import com.example.application.views.MainLayout;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import javax.swing.*;
import javax.validation.constraints.NotNull;

@Route(value = "customer-edit",layout = MainLayout.class)
public class CustomerEdit extends Main implements HasComponents, HasUrlParameter<String> {

    private TextField username;
    private TextField firstName;
    private TextField lastName;
    private EmailField emailField;
    private PasswordField password;
    private ComboBox<String> subscriptionType;
    private  final Button cancelButton = new Button("Cancel");
    private  final Button saveButton = new Button("Save Changes");
    private  final Button deleteButton = new Button("Delete Profile");
    private BeanValidationBinder<Customer> binder;
    private Customer customer;
    @NotNull
    private Customer originalCustomer;

    private ClientAgent clientAgent;


    @Override
    public void setParameter(BeforeEvent event, String parameter) {


        clientAgent = ClientAgent.getInstance();

        originalCustomer = VaadinSession.getCurrent().getAttribute(Customer.class);


        SplitLayout splitLayout = new SplitLayout();

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        binder = new BeanValidationBinder<>(Customer.class);

        binder.bindInstanceFields(this);

        createEditorLayout(horizontalLayout,originalCustomer);

        add(horizontalLayout);

    }

    private void createEditorLayout(HorizontalLayout horizontalLayoutLayout,Customer OriginalCustomer) {

        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        username = new TextField("Username");
        username.setValue(originalCustomer.getUsername());
        username.setRequired(true);
        binder.forField(username).withValidator(new StringLengthValidator("Username must be between 5 and 50 characters long",5,50))
                .bind(Customer::getUsername,Customer::setUsername);
        password = new PasswordField("Password");
        password.setRequired(true);
        binder.forField(password).bind(Customer::getPassword,Customer::generatePassword);
        firstName = new TextField("First Name");
        firstName.setValue(originalCustomer.getFirstName());
        firstName.setRequired(true);
        binder.forField(firstName).bind(Customer::getFirstName,Customer::setFirstName);
        lastName = new TextField("Last Name");
        lastName.setValue(originalCustomer.getLastName());
        lastName.setRequired(true);
        binder.forField(lastName).bind(Customer::getLastName,Customer:: setLastName);
        emailField = new EmailField("Email");
        emailField.setValue(OriginalCustomer.getEmail());
        emailField.setRequiredIndicatorVisible(true);
        binder.forField(emailField).withValidator(new EmailValidator( "Please enter a valid email address"))
                .bind(Customer::getEmail,Customer:: setEmail);
        subscriptionType = new ComboBox<>("Subscription Type");
        subscriptionType.setItems("BasicTier","StandardTier","PremiumTier");
        subscriptionType.setRequired(true);
        subscriptionType.setValue(originalCustomer.getSubscriptionType());
        subscriptionType.setRequiredIndicatorVisible(true);
        binder.forField(subscriptionType).bind(Customer::getSubscriptionType,Customer:: setSubscriptionType);
        formLayout.add(username,password,firstName, lastName,subscriptionType);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        addButtonLogic();

        horizontalLayoutLayout.add(editorLayoutDiv);

    }

    private void addButtonLogic()
    {
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        if (originalCustomer.getEmail().equals(VaadinSession.getCurrent().getAttribute(Customer.class).getEmail())) {
            saveButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
        saveButton.addClickListener(e -> {
           try {
               customer = new Customer();
               binder.writeBean(customer);
               customer.setPassword_saltFromString(originalCustomer.getPassword_saltAsString());
               customer.generatePassword(password.getValue());
              clientAgent.updateCustomer(customer);
               UI.getCurrent().navigate(CustomerInfoView.class,customer.getEmail());
               Notification.show("Profile info has been updated successfully");
           }
           catch (ValidationException validationException) {
               Notification.show("Failed to update the data. Check again that all values are valid");
           } catch (Exception exp) {
               Notification.show(exp.toString());
           }
        });
        deleteButton.addClickListener(e -> {
            try {
                clientAgent.deleteCustomer(originalCustomer);
                UI.getCurrent().navigate(LoginView.class);
                Notification.show("Profile info has been deleted successfully");
            } catch (Exception exp) {
                Notification.show("Something went wrong");
            }
        });

    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(saveButton, cancelButton,deleteButton);
        editorLayoutDiv.add(buttonLayout);
    }
}
