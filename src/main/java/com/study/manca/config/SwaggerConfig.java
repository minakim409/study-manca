package com.study.manca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger (OpenAPI) 설정
 * API 문서 자동 생성을 위한 설정 클래스
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("만화카페 관리 시스템 API")
                        .description("만화카페 운영을 위한 REST API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Manca Team")
                                .email("support@manca.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버")
                ));
    }
}
