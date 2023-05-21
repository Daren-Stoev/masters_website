package com.example.application.views.itemlist;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Product;
import com.example.application.data.services.ProductService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@PageTitle("Item List")
@Route(value = "item-list/:ItemID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
public class ItemListView extends Main implements HasComponents, HasStyle, BeforeEnterObserver {

    private final String ITEM_ID = "ItemID";
    private TextField searchBar;
    private Checkbox filter;
    private ProductService productService;
    private final Button createButton;
    private List<Product> products;
    private OrderedList productContainer;

    public ItemListView(ProductService productService) {
        this.productService = productService;
        constructUI();
        //items = itemService.findAllItems();
        products = productService.getAllProducts();
        filter = new Checkbox("Show only your items");
        //filter.addClickListener(e -> {});
        searchBar = new TextField("Type here to search");
        searchBar.addValueChangeListener(e -> {
            products = productService.findByNameStartsWithIgnoreCase(searchBar.getValue());
            productContainer.removeAll();
            for ( Product product : products ) {
                if(filter.getValue() && product.getOwner() == VaadinSession.getCurrent().getAttribute(Customer.class)){
                   productContainer.add(new ItemListViewCard(product));
                } else if (!filter.getValue())
                {
                    productContainer.add(new ItemListViewCard(product));
                }
            }
        });
        createButton = new Button("Add item");
        createButton.addClickListener(e ->
        {
            UI.getCurrent().navigate(ItemNewView.class);
        });
        add(searchBar,filter,createButton);
        if (products != null){
            for ( Product product : products) {
                productContainer.add(new ItemListViewCard(product));
            }}
    }

    private void constructUI() {
        addClassNames("image-list-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Beautiful photos");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Royalty free photos and pictures, courtesy of Unsplash");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Popularity", "Newest first", "Oldest first");
        sortBy.setValue("Popularity");

        productContainer = new OrderedList();
        productContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, sortBy);
        add(container, productContainer);

    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
      /*  Optional<Long> ItemId = event.getRouteParameters().get(ITEM_ID).map(Long::parseLong);
        if (ItemId.isPresent()) {
            Optional<Item> ItemFromBackend = itemService.get(ItemId.get());
            if (ItemFromBackend.isPresent()) {
                items.add(ItemFromBackend.get());
            }
        }*/
    }
}
