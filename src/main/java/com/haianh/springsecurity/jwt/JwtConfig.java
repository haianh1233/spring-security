package com.haianh.springsecurity.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.springframework.http.HttpHeaders.*;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
@Data
@NoArgsConstructor
public class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfter;

    public String getAuthorizationHeader() {
        return AUTHORIZATION;
    }
}
