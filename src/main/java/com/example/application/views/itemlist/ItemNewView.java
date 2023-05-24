package com.example.application.views.itemlist;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.ImageUtils;
import com.example.application.data.entity.Product;
import com.example.application.data.services.ProductService;
import com.example.application.views.MainLayout;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.*;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.helger.commons.mock.CommonsAssert.assertEquals;

@PageTitle("Item-new")
@Route(value = "item-new", layout = MainLayout.class)
@Uses(Icon.class)
public class ItemNewView extends Div implements BeforeEnterObserver {


    //private final Grid<Users> grid = new Grid<>(Users.class, false);


    private TextField name;
    private TextArea description;
    private TextField price;
    private Upload imageUpload;
    private ComboBox<String> category;
    private TextField quantity;
    private MemoryBuffer imageBuffer;

    private String imageUrl;

    private  final Button cancelButton = new Button("Cancel");
    private  final Button saveButton = new Button("Save");
    private final BeanValidationBinder<Product> binder;
    private Product product;
    private final ProductService productService;

    public ItemNewView(ProductService productService) {
        this.productService = productService;
        addClassNames("item-new-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();


        binder = new BeanValidationBinder<>(Product.class);

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
        name = new TextField("Product Name");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        binder.forField(name).withValidator(new StringLengthValidator("Name must be between 3 and 50 characters",3,50))
                        .bind(Product::getName,Product::setName);
        price = new TextField("Price");
        price.setRequired(true);
        price.setRequiredIndicatorVisible(true);
        binder.forField(price).withConverter(new StringToDoubleConverter("Please Enter a Valid number"))
                .withValidator(new DoubleRangeValidator("Price must be between 0 and 150000", 0.01, 150000.0))
                .bind(Product::getPrice, Product::setPrice);

        description = new TextArea("Description");
        description.setRequired(true);
        binder.forField(description)
                .withValidator(new StringLengthValidator("Description must be between 3 and 1000 characters", 3, 1000))
                .bind(Product::getDescription, Product::setDescription);

        category = new ComboBox<>("Category");
        category.setItems("Clothing","Apparel","Shoes","Computers","Kitchen","Music","Movies","Hobbies","Pets");
        category.setRequired(true);
        category.setRequiredIndicatorVisible(true);
        binder.forField(category).bind(Product::getCategory, Product::setCategory);

        quantity = new TextField("Stock quantity");
        quantity.setRequired(true);
        quantity.setRequiredIndicatorVisible(true);
        binder.forField(quantity).withConverter(new StringToIntegerConverter("Please enter a valid number"))
                .withValidator(new IntegerRangeValidator("Quantity must be between 0 and 100", 0, 100))
                .bind(Product::getQuantity, Product::setQuantity);



        imageBuffer = new MemoryBuffer();
        imageUpload = new Upload(imageBuffer);
        imageUpload.setMaxFiles(1);
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png");
        imageUpload.setAutoUpload(true);
        imageUpload.addSucceededListener(event -> {
            try {
                InputStream imageStream = imageBuffer.getInputStream();
                File imageFile = new File(event.getFileName());
                Files.copy(imageStream, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Save the image file using the ImageUtils class
               this.imageUrl = ImageUtils.saveImageToFile(imageFile);


                // ...
            } catch (IOException e) {
                // Handle any exceptions related to file processing
                e.printStackTrace();
            }
        });
        formLayout.add(name,price,description,category,imageUpload,quantity);

        cancelButton.addClickListener(e -> {
            UI.getCurrent().navigate(ItemListView.class);
        });
        ;
        saveButton.setEnabled(false);
        binder.addStatusChangeListener(event -> saveButton.setEnabled(binder.isValid()));

        saveButton.addClickListener(e -> {
            try {
                if (this.product == null) {
                    this.product = new Product();}
                // Checks if the email is already in use
                System.out.println("Setting owner");
                product.setOwner(VaadinSession.getCurrent().getAttribute(Customer.class));

                if(product.getImageUrl() == null)
                {
                    product.setImageUrl(this.imageUrl);
                }
                System.out.println("Writing bean");
                //product.printValues();
                binder.writeBean(this.product);
                System.out.println("Saving product to ontology");
                productService.addProductToOntology(this.product);
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
