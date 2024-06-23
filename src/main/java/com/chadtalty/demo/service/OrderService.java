package com.chadtalty.demo.service;

import com.chadtalty.demo.dto.OrderDTO;
import com.chadtalty.demo.entity.Order;
import com.chadtalty.demo.mapper.OrderMapper;
import com.chadtalty.demo.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final PersonService personService;

    public OrderDTO getOrderById(Long id) {
        return OrderMapper.toDTO(
                orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order not found.")));
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderMapper::toDTO).collect(Collectors.toList());
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {

        final Order order = OrderMapper.toEntity(orderDTO, personService.getPersonById(orderDTO.getPersonId()));

        order.getOrderItems().forEach(item -> item.setOrder(order));
        return OrderMapper.toDTO(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
