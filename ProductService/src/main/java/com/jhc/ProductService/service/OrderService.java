package com.jhc.ProductService.service;


import com.jhc.ProductService.model.OrderRequest;
import com.jhc.ProductService.model.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);

    List<OrderResponse> findAllOrdersByUserId(long userId);
}
