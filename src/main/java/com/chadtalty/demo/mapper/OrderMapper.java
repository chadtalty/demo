package com.chadtalty.demo.mapper;

import com.chadtalty.demo.dto.OrderDTO;
import com.chadtalty.demo.dto.OrderItemDTO;
import com.chadtalty.demo.entity.Order;
import com.chadtalty.demo.entity.OrderItem;
import com.chadtalty.demo.entity.Person;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setPersonId(order.getPerson().getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderItems(order.getOrderItems().stream().map(OrderMapper::toDTO).collect(Collectors.toList()));

        return dto;
    }

    public static Order toEntity(OrderDTO dto, Person person) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setPerson(person);
        order.setOrderDate(dto.getOrderDate());
        order.setOrderItems(
                dto.getOrderItems().stream().map(OrderMapper::toEntity).collect(Collectors.toList()));

        return order;
    }

    public static OrderItemDTO toDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setProductName(orderItem.getProductName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());

        return dto;
    }

    public static OrderItem toEntity(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProductName(dto.getProductName());
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPrice(dto.getPrice());

        return orderItem;
    }
}
