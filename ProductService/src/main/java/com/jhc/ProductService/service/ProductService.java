package com.jhc.ProductService.service;

import com.jhc.ProductService.model.ProductRequest;
import com.jhc.ProductService.model.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    Long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
