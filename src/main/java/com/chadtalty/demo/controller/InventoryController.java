package com.chadtalty.demo.controller;

import com.chadtalty.demo.dto.OrderDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InventoryController {

    @PostMapping("/check-inventory")
    public Boolean checkInventory(@RequestBody OrderDTO person) {
        return true;
    }
}
