package com.njf.cpuhigh.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CPU问题排查案例 API")
                        .version("1.0.0")
                        .description("这是一个专门用于学习CPU问题排查的Spring Boot应用，模拟了多种常见的CPU飙高场景")
                        .contact(new Contact()
                                .name("CPU问题排查学习项目")
                                .email("example@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
