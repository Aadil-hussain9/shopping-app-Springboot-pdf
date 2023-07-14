package com.jhc.ProductService.service.impl;

import com.jhc.ProductService.entity.Product;
import com.jhc.ProductService.exception.ProductServiceCustomException;
import com.jhc.ProductService.model.ProductRequest;
import com.jhc.ProductService.model.ProductResponse;
import com.jhc.ProductService.repository.ProductRepository;
import com.jhc.ProductService.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("Adding product");
        Product product = Product.builder().ProductName(productRequest.getName()).
                quantity(productRequest.getQuantity()).price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("product created");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for productId: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product with given id not found","Product_NOT_FOUND"));

        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);

        return productResponse;
    }


    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce quantity {} for Id: {}",quantity,productId);
        Product product =
                productRepository.findById(productId)
                        .orElseThrow(()->
                                new ProductServiceCustomException("Product with given Id not found","PRODUCT_NOT_FOUND"));
        if(product.getQuantity() < quantity)
        {
            throw new ProductServiceCustomException("Product does not have sufficient quantity","INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product quantity updated successfully");
    }
}

