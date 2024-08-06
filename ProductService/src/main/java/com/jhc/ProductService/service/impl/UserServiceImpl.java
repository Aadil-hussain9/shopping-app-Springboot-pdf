package com.jhc.ProductService.service.impl;

import com.jhc.ProductService.entity.Address;
import com.jhc.ProductService.entity.Order;
import com.jhc.ProductService.entity.User;
import com.jhc.ProductService.exceptions.CustomException;
import com.jhc.ProductService.repository.AddressRepository;
import com.jhc.ProductService.repository.UserRepository;
import com.jhc.ProductService.service.InvoicePDFService;
import com.jhc.ProductService.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final InvoicePDFService invoicePDFService;

    public UserServiceImpl(UserRepository userRepository, AddressRepository addressRepository, InvoicePDFService invoicePDFService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.invoicePDFService = invoicePDFService;
    }

    @Override
    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()){
            throw new CustomException("this email already exists :" + user.getEmail(),"Already_exist",409);
        }
        else {
            List<Address> addresses = user.getAddresses();
            for (Address address : addresses) {
                address.setUser(user);
                addressRepository.save(address);
            }
            user.setAddresses(addresses);
            log.info("Saved user with name :{}",user.getName());
            return userRepository.save(user);
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<byte[]> generateInvoiceOfUser(long userId) throws IOException {
        User user = userRepository.findById(userId).get();
        List<Order> orders = user.getOrders();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        invoicePDFService.generateInvoice(user,orders,  outputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "invoice "+user.getName()+".pdf");
        return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
    }
}
