package com.example.bankanalysis;

import com.example.bankanalysis.cli.CliRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAnalysisApplication.class, args);
    }

    /**
     * 使用 Lambda 实现 Spring 内置的 CommandLineRunner 函数式接口。
     * 返回值类型明确写成 Spring 的 CommandLineRunner，避免与自定义类冲突。
     */
    @Bean
    public CommandLineRunner cliRunner(@Autowired CliRunner runner) {
        return args -> runner.run(args);
    }
}
