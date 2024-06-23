package com.chadtalty.demo.service;

import com.chadtalty.demo.client.InventoryClient;
import com.chadtalty.demo.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private InventoryClient inventoryClient;

    public boolean failsInventoryCheck(OrderDTO orderDTO) {
        return !inventoryClient.checkInventory(orderDTO);
    }
}
