package com.gridgain.training.spring;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.apache.ignite.Ignite;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class IgniteConfig extends WebMvcConfigurationSupport {
    @Bean(name = "igniteInstance")
    public Ignite igniteInstance(Ignite ignite) {
        return ignite;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/5.11.8/");
    }
    @Bean
    public IgniteConfigurer configurer() {
        return igniteConfiguration -> {
            igniteConfiguration.setClientMode(true);
        };
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SpringData Swagger")
                        .description("SpringData + REST + SwaggerUI")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringData Swagger Documentation")
                        .url("https://github.com/rdGridGain/spring-data-swagger"));
    }


}

