package com.jhc.ProductService.service;


import com.jhc.ProductService.model.OrderRequest;
import com.jhc.ProductService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
