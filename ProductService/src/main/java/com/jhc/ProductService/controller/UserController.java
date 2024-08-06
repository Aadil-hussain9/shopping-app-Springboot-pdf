package com.jhc.ProductService.controller;

import com.jhc.ProductService.entity.User;
import com.jhc.ProductService.model.RegisterUserDto;
import com.jhc.ProductService.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        User createdUser = userService.createUser(registerUserDto.toUser());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<User> findUserByEmail(@RequestParam String email){
        Optional<User> userOptional = userService.findUserByEmail(email);
        return userOptional.map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/generate-invoice/{userId}")
    public ResponseEntity<byte[]> generateInvoiceOfUser(@PathVariable long userId) throws IOException{
        return userService.generateInvoiceOfUser(userId);
    }
}
