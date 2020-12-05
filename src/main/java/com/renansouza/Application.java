package com.renansouza;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Nu Stocks - Stocks Portifolio",
                version = "0.1",
                description = "Nu Stocks API",
                license = @License(name = "Apache 2.0", url = "https://github.com/renansouza-dev/nu-stocks/blob/main/LICENSE"),
                contact = @Contact(url = "https://github.com/renansouza-dev", name = "Renan", email = "renansouza-dev@protonmail.com")
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }

}