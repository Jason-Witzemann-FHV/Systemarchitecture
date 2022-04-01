package at.fhv.lab1reference;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan("readside")
public class ReadSide {

    public static void main(String[] args) {
        SpringApplication.run(ReadSide.class, args);
    }


}
