package com.example.application.views.itemlist;

import com.example.application.data.agents.ClientAgent;
import com.example.application.data.entity.Customer;
import com.example.application.data.entity.ImageUtils;
import com.example.application.data.entity.Product;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Route(value = "product-edit",layout = MainLayout.class)
public class ProductEdit extends Main implements HasComponents, HasUrlParameter<String> {
    private TextField name;
    private TextArea description;
    private TextField price;
    private Upload imageUpload;
    private ComboBox<String> category;
    private TextField quantity;
    private MemoryBuffer imageBuffer;

    private String imageUrl;

    private  final Button cancelButton = new Button("Cancel");
    private  final Button saveButton = new Button("Save Changes");
    private BeanValidationBinder<Product> binder;
    private Product product;
    @NotNull
    private  Product originalProduct;
    private ClientAgent clientAgent;
    @Override
    public void setParameter(BeforeEvent event, String productId) {

        clientAgent = ClientAgent.getInstance();
        addClassNames("item-info-view");
        clientAgent.getProductFromOntology(productId,fetchedProduct -> originalProduct = fetchedProduct,
                () ->{
                    SplitLayout splitLayout = new SplitLayout();
                    HorizontalLayout horizontalLayout = new HorizontalLayout();


                    binder = new BeanValidationBinder<>(Product.class);

                    binder.bindInstanceFields(this);

                    //createGridLayout(horizontalLayout);
                    createEditorLayout(horizontalLayout,originalProduct);


                    add(horizontalLayout);});


    }
    private void createEditorLayout(HorizontalLayout horizontalLayoutLayout,Product originalProduct) {

        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        Div imageDiv = new Div();
        ImageUtils imageUtils = new ImageUtils();
        Image originalimage = imageUtils.renderImage(originalProduct.getImageUrl());
        originalimage.setWidth("10%");
        imageDiv.add(originalimage);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Product Name");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        name.setValue(originalProduct.getName());
        binder.forField(name).withValidator(new StringLengthValidator("Name must be between 3 and 50 characters",3,50))
                .bind(Product::getName,Product::setName);
        price = new TextField("Price");
        price.setRequired(true);
        price.setRequiredIndicatorVisible(true);
        price.setValue(originalProduct.getPrice().toString());
        binder.forField(price).withConverter(new StringToDoubleConverter("Please Enter a Valid number"))
                .withValidator(new DoubleRangeValidator("Price must be between 0 and 150000", 0.01, 150000.0))
                .bind(Product::getPrice, Product::setPrice);

        description = new TextArea("Description");
        description.setRequired(true);
        description.setValue(originalProduct.getDescription());
        binder.forField(description)
                .withValidator(new StringLengthValidator("Description must be between 3 and 1000 characters", 3, 1000))
                .bind(Product::getDescription, Product::setDescription);

        category = new ComboBox<>("Category");
        category.setItems("Clothing","Apparel","Shoes","Computers","Kitchen","Music","Movies","Hobbies","Pets");
        category.setRequired(true);
        category.setRequiredIndicatorVisible(true);
        category.setValue(originalProduct.getCategory());
        binder.forField(category).bind(Product::getCategory, Product::setCategory);

        quantity = new TextField("Stock quantity");
        quantity.setRequired(true);
        quantity.setRequiredIndicatorVisible(true);
        quantity.setValue(originalProduct.getQuantity().toString());
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
                this.imageUrl = imageUtils.saveImageToFile(imageFile);
                Image newImage = imageUtils.renderImage(this.imageUrl);
                imageDiv.remove(originalimage);
                imageDiv.add(newImage);
                // ...
            } catch (IOException e) {
                // Handle any exceptions related to file processing
                e.printStackTrace();
            }
        });
        imageDiv.add(imageUpload);
        imageDiv.setWidth("10%");
        formLayout.add(name,price,description,category,imageDiv,quantity);

        cancelButton.addClickListener(e -> {
            UI.getCurrent().navigate(ProductInfoView.class,originalProduct.getId());
        });
        ;
        saveButton.setEnabled(false);
        binder.addStatusChangeListener(event -> saveButton.setEnabled(binder.isValid()));

        saveButton.addClickListener(e -> {
            try {
                if (this.product == null) {
                    this.product = new Product(originalProduct.getId());}
                product.setOwner(VaadinSession.getCurrent().getAttribute(Customer.class));


                product.setImageUrl(this.imageUrl);
                System.out.println("Writing bean");

                binder.writeBean(this.product);
                System.out.println("Saving product to ontology");

                clientAgent.updateProduct(this.product);
                UI.getCurrent().navigate(ProductInfoView.class,product.getId());
                Notification.show("Data updated");

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
}
