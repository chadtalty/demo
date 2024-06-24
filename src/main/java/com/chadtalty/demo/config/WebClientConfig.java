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

/**
 * Configuration class for setting up WebClient with JWT authentication.
 * <p>
 * This class configures a {@link WebClient} bean that includes an authorization
 * header containing a JWT token for each request. The JWT token is retrieved
 * from the reactive security context.
 * </p>
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a {@link WebClient} bean.
     * <p>
     * The WebClient is configured to include an authorization header with a JWT
     * token in each request.
     * </p>
     *
     * @param webClientBuilder the {@link WebClient.Builder} used to create the
     *                         WebClient.
     * @return a configured {@link WebClient} instance.
     */
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.filter(addAuthHeaderFilterFunction()).build();
    }

    /**
     * Creates an {@link ExchangeFilterFunction} that adds the authorization header
     * with a JWT token to each request.
     * <p>
     * The JWT token is retrieved from the reactive security context. If the token
     * is missing, a {@link MissingJwtTokenException} is thrown.
     * </p>
     *
     * @return an {@link ExchangeFilterFunction} that adds the authorization header
     *         with a JWT token.
     */
    private ExchangeFilterFunction addAuthHeaderFilterFunction() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) securityContext.getAuthentication();
                    if (jwtAuthToken != null) {
                        return ClientRequest.from(clientRequest)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        String.format(
                                                "Bearer %s",
                                                jwtAuthToken.getToken().getTokenValue()))
                                .build();
                    } else {
                        throw new MissingJwtTokenException("JWT token is missing from the request");
                    }
                })
                .defaultIfEmpty(clientRequest));
    }
}
