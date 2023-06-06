package com.example.application.data.agents;

import com.example.application.views.login.LoginView;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Runtime;

public class JadeContainer {
    private AgentContainer container;

    public void startContainer() {
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
            AgentController clientAgent = mainContainer.createNewAgent("ClientAgent", "com.example.application.data.agents.ClientAgent", new Object[0]);
            AgentController customerAgent = mainContainer.createNewAgent("CustomerAgent", "com.example.application.data.agents.CustomerAgent", new Object[0]);

            clientAgent.start();
            customerAgent.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}