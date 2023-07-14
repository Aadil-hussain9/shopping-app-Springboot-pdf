package com.jhc.orderservice.config;

import com.jhc.orderservice.external.client.ErrorDecoder.CustoErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    ErrorDecoder errorDecoder()
    {
        return new CustoErrorDecoder();
    }



}
