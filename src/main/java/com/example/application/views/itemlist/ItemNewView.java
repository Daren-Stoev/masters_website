package com.example.application.views.itemlist;

import com.example.application.data.entity.Item;
import com.example.application.data.entity.Users;
import com.example.application.data.service.ItemService;
import com.example.application.views.MainLayout;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.validator.*;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Item-new")
@Route(value = "item-new", layout = MainLayout.class)
@Uses(Icon.class)
public class ItemNewView extends Div implements BeforeEnterObserver {


    //private final Grid<Users> grid = new Grid<>(Users.class, false);


    private TextField name;
    private TextArea description;
    private TextField price;
    private TextField image;
    private  final Button cancelButton = new Button("Cancel");
    private  final Button saveButton = new Button("Save");
    private final BeanValidationBinder<Item> binder;
    private Item item;
    private final ItemService itemService;

    public ItemNewView(ItemService itemService) {
        this.itemService = itemService;
        addClassNames("item-new-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();


        binder = new BeanValidationBinder<>(Item.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        //createGridLayout(horizontalLayout);
        createEditorLayout(horizontalLayout);

        add(horizontalLayout);

        // Configure Form



    }

    private void createEditorLayout(HorizontalLayout horizontalLayoutLayout) {

        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Item Name");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        binder.forField(name).withValidator(new StringLengthValidator("Name must be between 3 and 50 characters",3,50))
                        .bind(Item::getName,Item::setName);
       /* price = new TextField("Price");
        price.setRequired(true);
        price.setRequiredIndicatorVisible(true);
        binder.forField(price).withConverter(String::valueOf, Float::valueOf, "Please enter a valid number").withValidator(new DoubleRangeValidator("Price must be between 0 and 150000",0.0,150000.0))
                        .bind(Item::getPrice, Item::setPrice);*/
        price = new TextField("Price");
        price.setRequired(true);
        price.setRequiredIndicatorVisible(true);
        binder.forField(price);
        binder.forField(price).withConverter(new StringToDoubleConverter("Please Enter a Valid number"))
                .withValidator(new DoubleRangeValidator("Price must be between 0 and 150000", 0.01, 150000.0))
                .bind(Item::getPrice, Item::setPrice);

        description = new TextArea("Description");
        description.setRequired(true);
        binder.forField(description)
                .withValidator(new StringLengthValidator("Description must be between 3 and 1000 characters", 3, 1000))
                .bind(Item::getDescription, Item::setDescription);

        image = new TextField("Image URL");
        image.setRequired(true);
        binder.forField(image);
        formLayout.add(name,price,description,image);

        cancelButton.addClickListener(e -> {
            UI.getCurrent().navigate(ItemListView.class);
        });
        ;
        saveButton.setEnabled(false);
        binder.addStatusChangeListener(event -> saveButton.setEnabled(binder.isValid()));

        saveButton.addClickListener(e -> {
            try {
                if (this.item == null) {
                    this.item = new Item();}
                // Checks if the email is already in use
                item.setUser(VaadinSession.getCurrent().getAttribute(Users.class));
                /*if(price.getValue() == null)
                {
                    item.setPrice(5.0f);
                }
                else {*/
                    item.setPrice(Double.parseDouble(price.getValue()));
                Notification.show(price.getValue());
                item.setName(name.getValue());
                item.setImageUrl(image.getValue());
                item.setDescription(description.getValue());
                if(item.getImageUrl() == null)
                {
                    item.setImageUrl("https://ichef.bbci.co.uk/news/976/cpsprodpb/A716/production/_95147724_kneeache.jpg");
                }
                binder.writeBean(this.item);
                itemService.update(this.item);
                UI.getCurrent().navigate(ItemListView.class);
                Notification.show("Data updated");
                UI.getCurrent().navigate(ItemListView.class);
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


        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        horizontalLayoutLayout.add(editorLayoutDiv);

    }
    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(saveButton, cancelButton);
        editorLayoutDiv.add(buttonLayout);
    }





    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    }
}
