package com.jhc.ProductService.service.impl;

import com.jhc.ProductService.entity.User;
import com.jhc.ProductService.repository.UserRepository;
import com.jhc.ProductService.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
