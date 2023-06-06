package com.example.application;

import ch.qos.logback.core.net.server.Client;
import com.example.application.data.agents.ClientAgent;
import com.example.application.data.agents.CustomerAgent;
import com.example.application.data.agents.ProductAgent;
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

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

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile profile = new ProfileImpl();

        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");
        profile.setParameter(Profile.GUI, "true");

        AgentContainer mainContainer =
                rt.createMainContainer(profile);

        if (mainContainer == null)
        {
            System.err.println("Error creating agent container");
            return;
        }



        try {
            //TestAgent.setUp((TestAgent) mainContainer.createNewAgent("ClientAgent", "com.example.application.data.agents.ClientAgent", new Object[0]));
            //AgentController clientAgent = mainContainer.createNewAgent("ClientAgent", "com.example.application.data.agents.ClientAgent", new Object[0]);
            //ClientAgent.setInstance((ClientAgent) mainContainer.createNewAgent("ClientAgent", "com.example.application.data.agents.ClientAgent", new Object[0]));
            //AgentController customerAgent = mainContainer.createNewAgent("CustomerAgent", "com.example.application.data.agents.CustomerAgent", new Object[0]);

            mainContainer.acceptNewAgent("ClientAgent", ClientAgent.getInstance());
            mainContainer.getAgent("ClientAgent").start();
            mainContainer.acceptNewAgent("CustomerAgent", CustomerAgent.getInstance());
            mainContainer.getAgent("CustomerAgent").start();
            mainContainer.acceptNewAgent("ProductAgent", ProductAgent.getInstance());
            mainContainer.getAgent("ProductAgent").start();
            // Set the instance of the ClientAgent
           // ClientAgent.setInstance((ClientAgent) clientAgent.getO2AInterface(ClientAgent.class));
        } catch (Exception e) {
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

/*
    @Bean
    public CommandLineRunner startJadeContainer() {
        return args -> {
            JadeContainer jadeContainer = new JadeContainer();
            jadeContainer.startContainer();
        };
    }
 */


/*
    @Bean
    public ClientAgent clientAgent() {
        //return new ClientAgent(agentContainer);
       return ClientAgent.getInstance();
    }*/





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
