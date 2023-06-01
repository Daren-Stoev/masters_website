package com.example.application.views.itemlist;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Order;
import com.example.application.data.entity.Product;
import com.example.application.data.services.OrderService;
import com.example.application.data.services.ProductService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Route(value = "product-info",layout = MainLayout.class)
public class ProductInfoView extends Main implements HasComponents,HasUrlParameter<String> {
    private String productId;

    private Product product;
    private ProductService productService;
    private OrderService orderService;

    private Order order;

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.productId = parameter;
        productService = new ProductService();
        product = productService.getProductById(productId);
        constructUI();
        Button buyButton = new Button("Buy");
        Button editButton = new Button("Edit");
        Button reportButton = new Button("Report");
        Button deleteButton = new Button("Delete");
        Button backButton = new Button("Back");


        // Add click listeners to the buttons
        buyButton.addClickListener(e -> {
            orderService = new OrderService();
            Customer orderCustomer = VaadinSession.getCurrent().getAttribute(Customer.class);
            order = new Order(product,orderCustomer);
            orderService.AddOrder(order);
            Notification.show("Product Added To Cart");
        });

        editButton.addClickListener(e -> {
            UI.getCurrent().navigate(ProductEdit.class, product.getId());
        });

        reportButton.addClickListener(e -> {
            // Handle report button click
        });

        backButton.addClickListener(e -> {
            UI.getCurrent().navigate(ItemListView.class);
        });
        deleteButton.addClickListener(e -> {
            productService.deleteProduct(product);
            Notification.show("Product Deleted");
            UI.getCurrent().navigate(ItemListView.class);
        });

        if(product.getOwner().getEmail().
                equals(
                        (VaadinSession.getCurrent().getAttribute(Customer.class)).getEmail())) {
            add(editButton, deleteButton, backButton);
        }
        else {
            add(buyButton, reportButton, backButton);
        }
    }

    private void constructUI() {
        // Create layout components
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);

        // Title section
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        H1 title = new H1(product.getName());
        titleLayout.add(title, new Span("By " + product.getOwner().getUsername()));
        mainLayout.add(titleLayout);

        // Description section
        Div descriptionDiv = new Div();
        Paragraph description = new Paragraph(product.getDescription());
        descriptionDiv.add("Description : " + description.getText());
        mainLayout.add(descriptionDiv);

        // Image section
        Div imageDiv = new Div();
        imageDiv.setWidth("100%");
        try {
            String resourcePath = "src/files/images/products/" + product.getImageUrl();
            File imageFile = new File(resourcePath);
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            StreamResource resource = new StreamResource(product.getImageUrl(), () -> fileInputStream);
            Image image = new Image(resource, "Alt Text");
            image.setWidth("25%");
            imageDiv.add(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mainLayout.add(imageDiv);

        // Price section
        Span price = new Span("Price: " + product.getPrice());
        mainLayout.add(price);

        // Add the main layout to the page
        add(mainLayout);
    }

}
