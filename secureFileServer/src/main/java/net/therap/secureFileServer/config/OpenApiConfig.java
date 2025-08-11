package net.therap.secureFileServer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author avidewan
 * @since 8/11/25
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String clientKeyScheme = "Client Key";
        String signatureScheme = "HMAC Signature";
        String timestampScheme = "Request Timestamp";

        return new OpenAPI()
                .info(new Info()
                        .title("Secure File Server API")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(clientKeyScheme)
                        .addList(signatureScheme))
                .components(new Components()
                        .addSecuritySchemes(clientKeyScheme,
                                new SecurityScheme()
                                        .name("X-Api-Key")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Name of the calling client (e.g. learningProcessorClient)"))
                        .addSecuritySchemes(signatureScheme,
                                new SecurityScheme()
                                        .name("X-Signature")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("HMAC signature for the request"))
                        .addSecuritySchemes(timestampScheme,
                                new SecurityScheme()
                                        .name("X-Timestamp")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Unix epoch time (seconds) when request is created")));
    }
}