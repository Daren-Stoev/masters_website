package com.example.application;

import com.example.application.data.agents.ClientAgent;
import com.example.application.data.ontologies.CustomerOntology;
import com.example.application.data.ontologies.ProductOntology;
import com.example.application.data.services.CustomerService;
import com.example.application.data.services.OrderService;
import com.example.application.data.services.ProductService;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import javax.sql.DataSource;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "myapp", variant = Lumo.DARK)
@NpmPackage(value = "line-awesome", version = "1.3.0")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
public class Application implements AppShellConfigurator {

    //private final AgentContainer agentContainer;

    //@Autowired
    //private Runtime runtime;



    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile profile = new ProfileImpl();

        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");
        profile.setParameter(Profile.GUI, "true");

        AgentContainer mainContainer =
                rt.createMainContainer(profile);

        try {
            AgentController ag = mainContainer.createNewAgent("Client",
                    "com.example.application.data.agents.ClientAgent", null);

            AgentController ag2 = mainContainer.createNewAgent("Customer",
                    "com.example.application.data.agents.CustomerAgent", null);

            ag.start();
            ag2.start();

        } catch (StaleProxyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // run the Spring Boot application
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public CustomerOntology customerOntology() {
        // Create and configure your CustomerOntology instance here
        return new CustomerOntology();
    }

    @Bean
    public Runtime runtime() {
        return Runtime.instance();
    }

    @Bean
    public ProductOntology productOntology() {
        // Create and configure your CustomerOntology instance here
        return new ProductOntology();
    }
    @Bean
    public ProductService productService() {
        return new ProductService();
    }
    @Bean
    public OrderService orderService() {
        // Create and configure your CustomerService instance here
        return new OrderService();
    }

    @Bean
    public CustomerService customerService() {
        // Create and configure your CustomerService instance here
        return new CustomerService();
    }

    @Bean
    @Autowired
    public AgentContainer agentContainer() {
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime().createMainContainer(profile);
        try {
            //agentContainer.acceptNewAgent("clientAgent", new ClientAgent(agentContainer));
            agentContainer.acceptNewAgent("clientAgent", new ClientAgent());
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        return agentContainer;
    }
    @Bean
    public ClientAgent appClientAgent(AgentContainer agentContainer) {
        //return new ClientAgent(agentContainer);
        return new ClientAgent();
    }



    /*
    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
            SqlInitializationProperties properties, CustomerOntology customerOntology) {
        // This bean ensures the database is only initialized when empty
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                if (repository.count() == 0L) {
                    return super.initializeDatabase();
                }
                return false;
            }
        };
    }
    */
}
