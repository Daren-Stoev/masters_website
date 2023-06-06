package com.example.application.data.agents;

import jade.core.Agent;

public class TestAgent extends Agent {

    static TestAgent instance = null;
    private TestAgent(){

    }


    public static void setUp(TestAgent instance) throws Exception
    {
        TestAgent.instance = instance;
    }
    public static TestAgent getInstance(){
        if(instance == null){
            instance = new TestAgent();
        }

        return instance;
    }

    @Override
    protected void setup() {

    }
}
