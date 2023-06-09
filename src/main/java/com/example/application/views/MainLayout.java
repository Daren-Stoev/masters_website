package com.example.application.views;


import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.data.entity.Customer;
import com.example.application.security.AuthService;
import com.example.application.views.about.AboutView;
import com.example.application.views.cardlist.CardListView;
import com.example.application.views.checkoutform.CheckoutFormView;
import com.example.application.views.customers.CustomerInfoView;
import com.example.application.views.customers.CustomerInfoViewRedirect;
import com.example.application.views.empty.EmptyView;
import com.example.application.views.helloworld.HelloWorldView;
import com.example.application.views.imagelist.ImageListView;
import com.example.application.views.itemlist.ItemListView;
import com.example.application.views.login.LoginView;
import com.example.application.views.logout.LogoutView;
import com.example.application.views.orders.OrderView;
import com.example.application.views.signup.SignUpView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private final AuthService authService;
    private H2 viewTitle;

    public MainLayout(AuthService authService) {
        this.authService = authService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        /*H1 logo = new H1("Vaadin CRM");
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");*/


        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true,toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("My App");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Hello World", HelloWorldView.class, "la la-globe"));
        nav.addItem(new AppNavItem("About", AboutView.class, "la la-file"));
        nav.addItem(new AppNavItem("Image List", ImageListView.class, "la la-th-list"));
        nav.addItem(new AppNavItem("Card List", CardListView.class, "la la-list"));
        nav.addItem(new AppNavItem("Checkout Form", CheckoutFormView.class, "la la-credit-card"));
        nav.addItem(new AppNavItem("Empty", EmptyView.class, "la la-file"));
        nav.addItem(new AppNavItem("Sign-up", SignUpView.class, "la la-file"));
        nav.addItem(new AppNavItem("Items", ItemListView.class, "la la-globe"));
        nav.addItem(new AppNavItem("Orders", OrderView.class, "la la-globe"));
        nav.addItem(new AppNavItem("Logout", LogoutView.class, "la la-globe"));
        nav.addItem(new AppNavItem("Profile Info",CustomerInfoView.class, "la la-globe"));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private Component[] createRoutes(){
       var customer =  VaadinSession.getCurrent().getAttribute(Customer.class);
        //Add some sorf for authorization
        return authService.getAuthorizedRoutes(1)
                .stream()
                .map(r -> createTab(r.name(),r.view()))
                .toArray(Component[] ::new);
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text,navigationTarget));
        ComponentUtil.setData(tab,Class.class,navigationTarget);
        return tab;
    }
}
