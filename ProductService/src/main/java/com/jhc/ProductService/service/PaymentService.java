package com.jhc.ProductService.service;


import com.jhc.ProductService.model.PaymentRequest;
import com.jhc.ProductService.model.PaymentResponse;

public interface PaymentService {
    public long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
