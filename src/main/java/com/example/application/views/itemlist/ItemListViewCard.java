package com.example.application.views.itemlist;

import com.example.application.data.entity.Product;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class ItemListViewCard extends ListItem {

    private Product product;

    public ItemListViewCard(Product product) {
        this.product = product;
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START, LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(LumoUtility.Background.CONTRAST, LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);
        div.setHeight("160px");

        String resourcePath = "src/files/images/products/" + product.getImageUrl();
        try {
            File imageFile = new File(resourcePath);
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            StreamResource resource = new StreamResource(product.getImageUrl(), () -> fileInputStream);
            Image image = new Image(resource, "Alt Text");
            image.setWidth("100%");
            div.add(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Span header = new Span();
        header.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);
        header.setText(product.getName());

        Paragraph description = new Paragraph(product.getDescription());
        description.addClassNames(LumoUtility.Margin.Vertical.MEDIUM);

        Paragraph price = new Paragraph("Price: " + product.getPrice());
        price.addClassNames(LumoUtility.Margin.Vertical.MEDIUM);

        Span badge = new Span();
        badge.getElement().setAttribute("theme", "badge");
        badge.setText("Label");
        addClickListener(event -> {
            // Redirect to the product info page
            UI.getCurrent().navigate(ProductInfoView.class, product.getId());
        });


        add(div, header, description, price, badge);
    }
}
