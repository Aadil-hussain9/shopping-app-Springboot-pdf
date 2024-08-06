package com.jhc.ProductService.service;

import com.jhc.ProductService.entity.User;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;


public interface UserService {
    User createUser(User user);

    Optional<User> findUserByEmail(String email);

    ResponseEntity<byte[]> generateInvoiceOfUser(long userId) throws IOException;

}
