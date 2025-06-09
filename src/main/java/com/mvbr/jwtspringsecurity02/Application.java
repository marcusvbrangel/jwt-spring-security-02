package com.mvbr.jwtspringsecurity02;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "API JWT Spring Security",
        version = "1.0",
        description = "API de exemplo com autenticação JWT, Spring Security, controle de acesso e arquitetura limpa."
    )
)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
