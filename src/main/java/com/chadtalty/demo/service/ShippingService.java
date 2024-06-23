package com.chadtalty.demo.service;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {

    private static final String SHIPPING_TOPIC = "shipping-service-topic";

    public void notify(UUID orderId) {}
}
