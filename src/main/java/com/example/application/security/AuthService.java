package com.example.application.security;

import com.example.application.data.entity.Customer;
import com.example.application.data.ontologies.CustomerOntology;
import com.example.application.views.MainLayout;
import com.example.application.views.about.AboutView;
import com.example.application.views.checkoutform.CheckoutFormView;
import com.example.application.views.customers.CustomerInfoView;
import com.example.application.views.customers.CustomerInfoViewRedirect;
import com.example.application.views.empty.EmptyView;
import com.example.application.views.helloworld.HelloWorldView;
import com.example.application.views.imagelist.ImageListView;
import com.example.application.views.itemlist.ItemListView;
import com.example.application.views.logout.LogoutView;
import com.example.application.views.signup.SignUpView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class AuthService {


    public record AuthorizedRoute(String route, String name, Class<? extends Component> view)
    {

    }

    public class AuthException extends Exception {
    }

    private final CustomerOntology customerOntology;

    public AuthService(CustomerOntology customerOntology) {
        this.customerOntology = customerOntology;
    }
    public boolean authenticate(String email, String password,Customer customer) throws AuthException{
       // Users user = userService.findByUsername(username);


        if(customer != null && customer.verifyPassword(password))
        {
            VaadinSession.getCurrent().setAttribute(Customer.class, customer);
            // Some service for admins i guess? I don't remember.'
            createRoutes(1);
            return true;
        }
        return false;
        //VaadinSession.getCurrent().setAttribute(Customer.class, customer);
        //createRoutes(dummy.getRole());

    }

    public void createRoutes (int role)
    {
    getAuthorizedRoutes(role).stream().
            forEach(route ->
                    RouteConfiguration
                            .forSessionScope()
                                .setRoute(route.route,route.view, MainLayout.class));
    }

    public List<AuthorizedRoute> getAuthorizedRoutes(int role)
    {
        ArrayList<AuthorizedRoute> routes = new ArrayList<>();
        /* Check the role of the account (user/admin/etc.) Not needed for now.
        if(role == 0)
        {
        maybe remove some of these later
        routes.add(new AuthorizedRoute("item-list","Item-List", ItemListView.class));
        routes.add(new AuthorizedRoute("sign-up","Sign-up", SignUpView.class));
        routes.add(new AuthorizedRoute("about","About", AboutView.class));
        routes.add(new AuthorizedRoute("hello-world","Hello-World", HelloWorldView.class));
        routes.add(new AuthorizedRoute("master","Master", MasterDetailView.class));
        routes.add(new AuthorizedRoute("empty","Empty", EmptyView.class));
        routes.add(new AuthorizedRoute("checkout","Checkout", CheckoutFormView.class));
        routes.add(new AuthorizedRoute("image-list","Image-List", ImageListView.class));

        } else if (role == 1)
        {
        Add admin panels
        }*/
        routes.add(new AuthorizedRoute("item-list/:ItemID?/:action?(edit)","Item List", ItemListView.class));
        routes.add(new AuthorizedRoute("signup","Signup", SignUpView.class));
        routes.add(new AuthorizedRoute("about","About", AboutView.class));
        routes.add(new AuthorizedRoute("hello-world","Hello-World", HelloWorldView.class));
        routes.add(new AuthorizedRoute("empty","Empty", EmptyView.class));
        routes.add(new AuthorizedRoute("checkout-form","Checkout Form", CheckoutFormView.class));
        routes.add(new AuthorizedRoute("image-list","Image-List", ImageListView.class));
        routes.add(new AuthorizedRoute("card-list","Card List", ImageListView.class));
        routes.add(new AuthorizedRoute("logout","Logout", LogoutView.class));
        routes.add(new AuthorizedRoute("customer-info-redirect","Profile Info Redirect", CustomerInfoViewRedirect.class));



        return routes;
    }


}
