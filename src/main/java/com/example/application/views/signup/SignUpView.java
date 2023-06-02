package com.example.application.views.signup;


import com.example.application.data.entity.Customer;
import com.example.application.data.services.CustomerService;
import com.example.application.views.about.AboutView;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@Route("signup")
@PageTitle("SignUp")
//@Route(value = "signup/:UserID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class SignUpView extends Div implements BeforeEnterObserver {

    private final String USER_ID = "UserID";
    private final String USER_EDIT_ROUTE_TEMPLATE = "signup/%s/edit";

    private final Grid<Customer> grid = new Grid<>(Customer.class, false);

    private TextField username;
    private PasswordField password;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField subscriptionType;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final BeanValidationBinder<Customer> binder;
    private Customer customer;
    private final CustomerService customerService;

    public SignUpView(CustomerService customerService) {
        this.customerService = customerService;
        addClassNames("signup-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        //createGridLayout(horizontalLayout);
        createEditorLayout(horizontalLayout);

        add(horizontalLayout);

        // Configure Form
        binder = new BeanValidationBinder<>(Customer.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.customer == null) {
                    this.customer = new Customer();}
                // Checks if the email is already in use.
                binder.forField(email)
                        // Explicit validator instance
                        .withValidator(new EmailValidator(
                                "This doesn't look like a valid email address"))
                        .bind(Customer::getEmail, Customer::setEmail);
                if(customer != null) {
                    System.out.println(binder.getFields());
                    binder.writeBean(this.customer);
                    this.customer.setSubscriptionType("BasicTier");
                    if (customerService.EmailAlreadyExists(customer))
                    {
                        throw new Exception("This email is already in use");
                    }
                    customer.printInfo();
                    System.out.println("Customer");
                    customerService.addCustomerToOntology(this.customer);
                    UI.getCurrent().navigate(LoginView.class);
                }
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AboutView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            } catch (Exception exp) {
                Notification.show(exp.toString());
            }
        });
    }

    private void createEditorLayout(HorizontalLayout horizontalLayoutLayout) {

        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        username = new TextField("Username");
        password = new PasswordField("Password");
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        //subscriptionType = new TextField("Subscription Type");
        formLayout.add(username,password,firstName, lastName, email);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        horizontalLayoutLayout.add(editorLayoutDiv);

    }
    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Customer customer) {
        this.customer = customer;
        binder.readBean(this.customer);

    }




    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    }
}
