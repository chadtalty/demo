package com.chadtalty.demo.client;

import com.chadtalty.demo.dto.OrderDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private HttpServletRequest request;

    public boolean verifyPayment(OrderDTO orderDTO) {

        return webClientBuilder
                .build()
                .post()
                .uri("http://localhost:8080/api/verify-payment")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", request.getAttribute("jwt")))
                .bodyValue(orderDTO)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}
