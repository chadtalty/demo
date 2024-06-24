package com.chadtalty.demo.client;

import com.chadtalty.demo.dto.OrderDTO;
import com.chadtalty.demo.exception.WebClientException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class InventoryClient {

    @Value("${application.services.inventory.url}")
    private String inventoryServiceUri;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private HttpServletRequest request;

    public boolean checkInventory(OrderDTO orderDTO) throws WebClientException {
        return webClientBuilder
                .build()
                .post()
                .uri(inventoryServiceUri)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", request.getAttribute("jwt")))
                .bodyValue(orderDTO)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorMap(WebClientResponseException.class, e -> {
                    throw new WebClientException("Error response from inventory service", e);
                })
                .onErrorMap(WebClientRequestException.class, e -> {
                    throw new WebClientException("Request error when contacting inventory service", e);
                })
                .onErrorMap(Exception.class, e -> {
                    throw new WebClientException("Unexpected error occurred", e);
                })
                .block();
    }
}
