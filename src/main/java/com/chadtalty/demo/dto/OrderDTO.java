package com.chadtalty.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {

    @NotNull private UUID orderId;

    @NotNull(message = "Person ID is required") private Long personId;

    @NotNull(message = "Order date is required") private Date orderDate;

    @NotNull(message = "Order items are required") @NotEmpty(message = "Order items cannot be empty.")
    private List<@Valid OrderItemDTO> orderItems;
}
