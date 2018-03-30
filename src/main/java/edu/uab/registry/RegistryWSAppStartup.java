package edu.uab.registry;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RegistryWSAppStartup implements CommandLineRunner {

    public void run(String[] args) 
    {
        System.out.println("***  Inside RegistryWSAppStartup.run method for any service startup activity that needs to be done .....");
        //System.out.println("args.length=" + args.length);
    }

}
