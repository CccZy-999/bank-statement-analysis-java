package com.example.bankanalysis;

import com.example.bankanalysis.cli.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner as SpringCommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAnalysisApplication.class, args);
    }

    @Bean
    public SpringCommandLineRunner runner(@Autowired CommandLineRunner cliRunner) {
        return args -> cliRunner.run(args);
    }
}
