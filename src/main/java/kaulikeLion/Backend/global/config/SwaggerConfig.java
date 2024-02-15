package kaulikeLion.Backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Like Lion Api")
                .description("Like Lion API 명세서")
                .version("v1.0.0");

        return new OpenAPI()
                .components(new io.swagger.v3.oas.models.Components())
                .info(info);
    }
}
