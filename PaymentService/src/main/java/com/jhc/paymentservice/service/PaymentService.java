package com.jhc.paymentservice.service;

import com.jhc.paymentservice.model.PaymentRequest;
import com.jhc.paymentservice.model.PaymentResponse;

public interface PaymentService {
    public long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
