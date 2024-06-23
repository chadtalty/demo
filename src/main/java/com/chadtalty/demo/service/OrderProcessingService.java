package com.chadtalty.demo.service;

import com.chadtalty.demo.client.PaymentClient;
import com.chadtalty.demo.dto.OrderDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    @Autowired
    private PersonService personService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private ShippingService shippingService;

    public OrderDTO process(Long userId, OrderDTO orderDTO) {

        // Check inventory
        if (inventoryService.failsInventoryCheck(orderDTO)) {
            throw new RuntimeException("Inventory check failed");
        }

        // Verify payment
        if (!paymentClient.verifyPayment(orderDTO)) {
            throw new RuntimeException("Payment verification failed");
        }

        OrderDTO order = orderService.createOrder(orderDTO);

        shippingService.notify(order.getOrderId());

        return order;
    }

    public List<OrderDTO> getOrdersForUser(Long userId) {
        // Fetch orders for a specific user
        return userService.getUserById(userId).getOrders();
    }

    public void deleteAll(Long personId) {

        // Delete all orders related to the person
        personService.getPersonById(personId).getOrders().forEach(order -> orderService.deleteOrder(order.getId()));

        // Delete the person
        personService.deletePerson(personId);
    }
}
