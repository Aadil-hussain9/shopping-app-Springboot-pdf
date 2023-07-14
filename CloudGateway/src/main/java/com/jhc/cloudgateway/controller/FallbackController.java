package com.jhc.cloudgateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/productServiceFallBack")
    public String ProductServiceFallback()
    {
        return "Product Service is Down !!";
    }

    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallback()
    {
        return "Order Service is Down !!";
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentServiceFallBack()
    {
        return "Payment Service is Down !!";
    }
}
