package edu.uab.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class RegistryWSApp extends SpringBootServletInitializer 
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) 
    {
        return application.sources(RegistryWSApp.class);
    }

    public static void main(String[] args) throws Exception 
    {  
    	SpringApplication app = new SpringApplication(RegistryWSApp.class);    	    
        app.run(args);
    }
    
}
