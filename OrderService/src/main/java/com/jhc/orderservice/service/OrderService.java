package com.jhc.orderservice.service;

import com.jhc.orderservice.model.OrderRequest;
import com.jhc.orderservice.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
