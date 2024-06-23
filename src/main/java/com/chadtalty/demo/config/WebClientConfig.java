package com.chadtalty.demo.config;

import com.chadtalty.demo.exception.MissingJwtTokenException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.filter(addAuthHeaderFilterFunction()).build();
    }

    private ExchangeFilterFunction addAuthHeaderFilterFunction() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) securityContext.getAuthentication();
                    if (jwtAuthToken != null) {
                        ClientRequest clientRequestWithAuth = ClientRequest.from(clientRequest)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + jwtAuthToken.getToken().getTokenValue())
                                .build();
                        return clientRequestWithAuth;
                    } else {
                        throw new MissingJwtTokenException("JWT token is missing from the request");
                    }
                })
                .defaultIfEmpty(clientRequest));
    }
}
