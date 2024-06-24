package com.chadtalty.demo;

import com.chadtalty.demo.entity.*;
import com.chadtalty.demo.repository.*;
import java.util.Arrays;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public void run(String... args) throws Exception {

        // Create Persons
        Person person1 = new Person();
        person1.setUsername("person1");
        person1.setPassword("password1");
        person1.setEmail("person1@example.com");
        person1.setCreatedAt(new Date());
        person1.setCreatedBy("system");
        person1.setLastModifiedBy("system");

        Person person2 = new Person();
        person2.setUsername("person2");
        person2.setPassword("password2");
        person2.setEmail("person2@example.com");
        person2.setCreatedAt(new Date());
        person2.setCreatedBy("system");
        person2.setLastModifiedBy("system");

        personRepository.saveAll(Arrays.asList(person1, person2));

        // Create Addresses
        Address address1 = new Address();
        address1.setStreet("123 Main St");
        address1.setCity("Anytown");
        address1.setState("Anystate");
        address1.setZipCode("12345");
        address1.setPerson(person1);
        address1.setCreatedBy("system");
        address1.setLastModifiedBy("system");

        Address address2 = new Address();
        address2.setStreet("456 Oak St");
        address2.setCity("Othertown");
        address2.setState("Otherstate");
        address2.setZipCode("67890");
        address2.setPerson(person2);
        address2.setCreatedBy("system");
        address2.setLastModifiedBy("system");

        addressRepository.saveAll(Arrays.asList(address1, address2));

        // Create Orders
        Order order1 = new Order();
        order1.setPerson(person1);
        order1.setOrderDate(new Date());
        order1.setCreatedBy("system");
        order1.setLastModifiedBy("system");

        Order order2 = new Order();
        order2.setPerson(person2);
        order2.setOrderDate(new Date());
        order2.setCreatedBy("system");
        order2.setLastModifiedBy("system");

        orderRepository.saveAll(Arrays.asList(order1, order2));

        // Create Order Items
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setOrder(order1);
        orderItem1.setProductName("Product 1");
        orderItem1.setQuantity(2);
        orderItem1.setPrice(19.99);
        orderItem1.setCreatedBy("system");
        orderItem1.setLastModifiedBy("system");

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setOrder(order2);
        orderItem2.setProductName("Product 2");
        orderItem2.setQuantity(1);
        orderItem2.setPrice(29.99);
        orderItem2.setCreatedBy("system");
        orderItem2.setLastModifiedBy("system");

        orderItemRepository.saveAll(Arrays.asList(orderItem1, orderItem2));
    }
}
