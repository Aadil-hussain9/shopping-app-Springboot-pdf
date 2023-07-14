package com.jhc.orderservice.external.client;

import com.jhc.orderservice.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CircuitBreaker(name = "external",fallbackMethod = "fallback")
@FeignClient(name = "PRODUCT-SERVICE/product",url = "http://localhost:8080/product")
public interface ProductService {

    @PutMapping("/reduceQuantity/{id}")
    ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId ,
            @RequestParam long quantity
    );

    default void fallback(Exception e)
    {
        throw new CustomException("Product service is not available",
                "NOT-AVAILABLE",500);
    }
}
