package com.exposer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.In;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${api.base.url}")
    private String baseurl;

    @Bean
    public OpenAPI openAPI() {
        OpenAPI api = new OpenAPI();
        Info info = new Info();
        info.setTitle("Exposer Api");
        info.setDescription("Exposer Blog api using java and spring boot");
        info.setVersion("1.0.0");
        info.setContact(new Contact().email("bilalkhan.devse@gmail.com").name("Bilal Khan").url("https://bilalkhandev.vercel.app"));
        info.setVersion("v1.0.0");
        info.setLicense(new License().url("https://www.linkedin.com/in/muhammad-bilal-khan-83660931b/").name("LinkedIn"));
        List<Server> serverList = List.of(
                new Server().description("dev").url("http://localhost:8081"),
                new Server().description("prod").url(baseurl)
        );

        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .scheme("bearer")
                .type(Type.HTTP)
                .bearerFormat("JWT")
                .in(In.HEADER);

        Components components = new Components().addSecuritySchemes("Token", securityScheme);
        api.setServers(serverList);
        api.setSecurity(List.of(new SecurityRequirement().addList("Token")));
        api.setComponents(components);
        api.setInfo(info);
        return api;
    }

}
