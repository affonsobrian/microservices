package com.azvd.microservices.order.service;

import com.azvd.microservices.order.client.InventoryClient;
import com.azvd.microservices.order.dto.OrderRequest.OrderRequest;
import com.azvd.microservices.order.model.Order;
import com.azvd.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (isProductInStock) {
            // Request to model mapping
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            // Save the order to the database
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Product with SkuCode: " + orderRequest.skuCode() + " is out of stock");
        }
    }
}
