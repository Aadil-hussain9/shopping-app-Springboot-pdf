package com.jhc.ProductService.validator;

import com.jhc.ProductService.customAnnotation.UniqueEmail;
import com.jhc.ProductService.repository.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail,String> {

    private final UserRepository userRepository;
    public UniqueEmailValidator(UserRepository userRepository){

        this.userRepository = userRepository;
    }
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && userRepository.findByEmail(email).isEmpty();
    }
}
