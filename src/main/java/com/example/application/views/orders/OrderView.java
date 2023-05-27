package com.example.application.views.orders;

import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Order;
import com.example.application.data.entity.Product;
import com.example.application.data.services.OrderService;
import com.example.application.data.services.ProductService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Orders tables")
@Route(value = "orders", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends Main implements HasComponents, HasStyle, BeforeEnterObserver {

    private TextField searchBar;
    private Checkbox filter;
    private ProductService productService = new ProductService();
    private OrderService orderService = new OrderService();

    public OrderView(ProductService productService,OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
        Tab personalOrdersTab = generateTable("Orders",true,"");
        Tab yourProductsTab = generateTable("Your Products",false,"");
        searchBar = new TextField("Search");
        searchBar.addValueChangeListener(e -> refreshGrid(searchBar.getValue()));



        add(searchBar,personalOrdersTab,yourProductsTab);
    }

    private Tab generateTable(String TabName,Boolean personal,String searchQuery) {
        Tab tab = new Tab(TabName);
        VerticalLayout content = new VerticalLayout();
        List<Order> orders = fetchOrdersFromOntology(personal,searchQuery);

        System.out.println("Orders in View " + orders.size());
        double totalPrice = orders.stream()
                .mapToDouble(order -> order.getProduct().getPrice())
                .sum();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedTotalPrice = decimalFormat.format(totalPrice);

        Label totalLabel = new Label("Total Price: $" + formattedTotalPrice);
// Create a grid bound to the list
        Grid<Order> grid = new Grid<>();
        grid.setItems(orders);
        grid.addColumn(Order::getOrderNumber).setHeader("Order Number").setSortable(true);
        grid.addColumn(order -> order.getProduct().getName())
                .setHeader("Product Name").setSortable(true);

        grid.addColumn(new ComponentRenderer<>(order -> {
                String resourcePath = "src/files/images/products/" + order.getProduct().getImageUrl();
                try {
                    File imageFile = new File(resourcePath);
                    FileInputStream fileInputStream = new FileInputStream(imageFile);
                    StreamResource resource = new StreamResource(order.getProduct().getImageUrl(), () -> fileInputStream);
                    Image image = new Image(resource, "Alt Text");
                    image.setWidth("20%");
                    return image;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return new Image();
        }))
                .setHeader("Product Image");
        grid.addColumn(Order::getDatetime).setHeader("Timestamp").setSortable(true);
        if(personal) {
            grid.addColumn(order -> order.getProduct().getOwner().getUsername())
                    .setHeader("Seller").setSortable(true);
        }
        else {
            grid.addColumn(order -> order.getCustomer().getUsername())
                   .setHeader("Bought by").setSortable(true);
        }

        grid.addColumn(order -> order.getProduct().getPrice()).setHeader("Price");
        grid.addColumn(new NativeButtonRenderer<>("Remove item",
                        (order) -> {
                            orderService.removeOrder(order);
                            List<Order> updatedOrders = fetchOrdersFromOntology(personal, searchQuery);
                            grid.setItems(updatedOrders);
                        }))
                .setHeader("Actions");
        //total of all products from combining all "price" values


        content.add(grid,totalLabel);
        tab.add(content);
        return tab;
    }
    private List<Order> fetchOrdersFromOntology(Boolean personal,String searchQuery) {
        List<Order> orders = new ArrayList<>();
        if(personal) {
            orders = orderService.getOrdersByCustomer(VaadinSession.getCurrent().getAttribute((Customer.class)));
            System.out.println("orders personal: " + orders.size());
            if (searchQuery != null && !searchQuery.isEmpty())
            {
                List<Product> products = orders.stream().map(Order::getProduct).collect(Collectors.toList());
                products = productService.filterByNameContainsIgnoreCase(products,searchQuery);
                List<Product> finalProducts = products;
                orders = orders.stream().filter(order -> finalProducts.contains(order.getProduct())).collect(Collectors.toList());
            }
        }
        // We fetch all products that this user has uploaded and then get all orders with each product
        else {
            List<Product> products = this.productService.getProductsByCustomer(VaadinSession.getCurrent().getAttribute((Customer.class)));
            System.out.println("products: " + products.size());
            products = productService.filterByNameContainsIgnoreCase(products,searchQuery);
            for(Product product : products) {
                System.out.println(product.getName());
                ArrayList<Order> productOrders = orderService.getOrdersByProduct(product);
                System.out.println("Product orders" + productOrders.size());
                orders.addAll(productOrders);
            }
        }
        return orders;
    }

    private void refreshGrid(String searchQuery) {
        // Remove all components from the layout
        removeAll();

        // Generate the table with the updated search query
        Tab personalOrdersTab = generateTable("Orders", true, searchQuery);
        Tab yourProductsTab = generateTable("Your Products", false, searchQuery);

        // Add the components back to the layout
        add(searchBar, personalOrdersTab, yourProductsTab);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }
}
