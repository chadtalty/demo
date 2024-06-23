package com.chadtalty.demo.controller;

import com.chadtalty.demo.dto.OrderDTO;
import com.chadtalty.demo.service.OrderProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-processing")
@Tag(name = "Order Processing", description = "Operations related to processing orders")
public class OrderProcessingController {

    @Autowired
    private OrderProcessingService orderProcessingService;

    @PostMapping("/process-order/{userId}")
    @Operation(summary = "Process an order for a user")
    public ResponseEntity<OrderDTO> processOrder(@PathVariable Long userId, @Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO processedOrder = orderProcessingService.process(userId, orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(processedOrder);
    }

    @GetMapping("/orders/{userId}")
    @Operation(summary = "Get orders for a user")
    public ResponseEntity<List<OrderDTO>> getOrdersForUser(
            @Parameter(description = "ID of the order to verify payment", required = true, example = "123")
                    @PathVariable
                    Long userId) {
        List<OrderDTO> orders = orderProcessingService.getOrdersForUser(userId);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/delete-all/{personId}")
    @Operation(summary = "Delete a person and their orders")
    public ResponseEntity<Void> deleteAll(@PathVariable Long personId) {
        orderProcessingService.deleteAll(personId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
