package com.jhc.orderservice.service;

import brave.messaging.ProducerResponse;
import com.jhc.orderservice.entity.Order;
import com.jhc.orderservice.exception.CustomException;
import com.jhc.orderservice.external.client.PaymentService;
import com.jhc.orderservice.external.client.ProductService;
import com.jhc.orderservice.external.client.response.PaymentResponse;
import com.jhc.orderservice.model.OrderRequest;
import com.jhc.orderservice.model.OrderResponse;
import com.jhc.orderservice.model.PaymentRequest;
import com.jhc.orderservice.model.ProductResponse;
import com.jhc.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

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
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for Order Id :" + orderId,
                        "not_found", 404));

        log.info("invoking product service to fetch product for id {}", order.getProductId());
        ProductResponse productResponse =
                restTemplate.getForObject(
                        "http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class
                );
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId()).
                build();

        PaymentResponse paymentResponse =
                restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(), PaymentResponse.class);


        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentMode(paymentResponse.getPaymentMode())
                .status(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .build();

        OrderResponse orderResponse = OrderResponse.builder().
                orderId(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();


        return orderResponse;
    }
}
