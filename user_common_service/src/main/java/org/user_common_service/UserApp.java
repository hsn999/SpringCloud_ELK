package org.user_common_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * Hello world!
 *
 */
@SpringBootApplication
public class UserApp
{
    public static void main( String[] args )
    {
    	SpringApplication.run(UserApp.class, args);
    	System.out.println( "Hello World!" );
    }
}
