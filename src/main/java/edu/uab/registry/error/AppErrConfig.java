package edu.uab.registry.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.uab.registry.controller.AppErrorController;

@Configuration
public class AppErrConfig 
{
	@Autowired
	private ErrorAttributes errorAttributes;

	@Bean
	public AppErrorController appErrorController()
	{
		return new AppErrorController(errorAttributes);
	}
}
