package org.user_check_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * Hello world!
 *
 */
@SpringBootApplication
public class CheckApp
{
    public static void main( String[] args )
    {
    	SpringApplication.run(CheckApp.class, args);
    	System.out.println( "Hello World!" );
    }
}
