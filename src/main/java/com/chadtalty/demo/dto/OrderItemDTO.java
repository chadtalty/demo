package com.chadtalty.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDTO {

    @NotNull(message = "Order ID is required") private Long orderId;

    @NotEmpty(message = "Product name is required")
    private String productName;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is required") private Integer quantity;

    @Min(value = 0, message = "Price must be at least 0")
    @NotNull(message = "Price is required") private Double price;
}
