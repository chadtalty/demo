package com.chadtalty.demo.controller;

import com.chadtalty.demo.dto.OrderDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @PostMapping("/verify-payment")
    public Boolean verifyPayment(@RequestBody OrderDTO person) {
        return true;
    }
}
