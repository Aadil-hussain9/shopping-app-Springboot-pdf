package com.jhc.ProductService.service.impl;

import com.jhc.ProductService.entity.Order;
import com.jhc.ProductService.exceptions.CustomException;
import com.jhc.ProductService.model.*;
import com.jhc.ProductService.repository.OrderRepository;
import com.jhc.ProductService.service.OrderService;
import com.jhc.ProductService.service.PaymentService;
import com.jhc.ProductService.service.ProductService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final PaymentService paymentService;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService, PaymentService paymentService){
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //OrderEntity -> save order with status order created
        //Product service -> Block products(reduce the quantity)
        //payment service -> payments -> success ->complete ,else ->cancelled

        log.info("Placing Order request : {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with status created");
        Order order = Order.builder().amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling Payment Service to complete payment");
//
        PaymentRequest paymentRequest =
                PaymentRequest.builder()
                        .orderId(order.getId())
                        .paymentMode(orderRequest.getPaymentMode())
                        .amount(orderRequest.getTotalAmount()).build();
        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Success, Changing order status to placed");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error in payment , changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }


        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order places successfully with order Id: {} ", order.getId());
        return order.getId();
    }


    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for id:{}", orderId);
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for Order Id :" + orderId,
                        "not_found", 404));

        log.info("invoking product service to fetch product for id {}", order.getProductId());
        ProductResponse productResponse = productService.getProductById(order.getProductId());
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .quantity(productResponse.getQuantity())
                .price(productResponse.getPrice())
                .productId(productResponse.getProductId()).
                build();

        PaymentResponse paymentResponse = paymentService.getPaymentDetailsByOrderId(order.getId());
        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentMode(paymentResponse.getPaymentMode())
                .status(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .userId(paymentResponse.getUserId())
                .amount(paymentResponse.getAmount())
                .userId(paymentResponse.getUserId())
                .orderId(paymentResponse.getOrderId())
                .build();

        OrderResponse orderResponse = OrderResponse.builder().
                orderId(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .userId(order.getUser().getId())
                .build();


        return orderResponse;
    }

    @Override
    public List<OrderResponse> findAllOrdersByUserId(long userId) {
        List<Order> orderList = orderRepository.findByUserId(userId);
        return orderList.stream().map(order ->
                OrderResponse.builder()
                        .orderStatus(order.getOrderStatus())
                        .amount(order.getAmount())
                        .orderDate(order.getOrderDate())
                        .productDetails(OrderResponse.ProductDetails.builder().productId(order.getProductId()).build())
                        .build())
                .collect(Collectors.toList());
    }
}
