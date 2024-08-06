package com.jhc.ProductService.integrationTest;

import com.jhc.ProductService.entity.Address;
import com.jhc.ProductService.entity.User;
import com.jhc.ProductService.repository.AddressRepository;
import com.jhc.ProductService.repository.UserRepository;
import com.jhc.ProductService.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class serviceIntegrationTesting {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Before
    public void setup() {
        // Clean up the repositories before each test
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }


    @Test
    public void testCreateUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        Address address1 = new Address();
        address1.setStreet("123 Main St");
        address1.setCity("Springfield");
        address1.setZipCode("12345");

        Address address2 = new Address();
        address2.setStreet("456 Elm St");
        address2.setCity("Shelbyville");
        address2.setZipCode("67890");

        user.setAddresses(Arrays.asList(address1, address2));

        // Call the createUser method
        User createdUser = userService.createUser(user);

        // Assertions
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("John Doe", createdUser.getName());
        assertEquals("john.doe@example.com", createdUser.getEmail());
        assertEquals(2, createdUser.getAddresses().size());
    }
}
