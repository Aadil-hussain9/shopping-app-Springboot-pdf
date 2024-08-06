package com.jhc.ProductService.model;

import com.jhc.ProductService.customAnnotation.UniqueEmail;
import com.jhc.ProductService.entity.Address;
import com.jhc.ProductService.entity.User;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
public class RegisterUserDto {
    @NotEmpty(message = "The full name is required.")
    @Size(min = 2, max = 100, message = "The length of full name must be between 2 and 100 characters.")
    private String fullName;

    @NotEmpty(message = "The email address is required.")
    @Email(message = "The email address is invalid.", flags = { Pattern.Flag.CASE_INSENSITIVE })
    @UniqueEmail
    private String email;

    @NotNull(message = "The date of birth is required.")
    @Past(message = "The date of birth must be in the past.")
    private Date dateOfBirth;

    @NotEmpty(message = "The gender is required.")
    private String gender;

    @Valid
    @NotNull(message = "The address is required.")
    private AddressDto address;


    public User toUser(){
        return User.builder()
                .name(fullName)
                .email(email)
                .birthDate(dateOfBirth)
                .gender(gender)
                .addresses((List<Address>) address.toAddress())
                .build();
    }

}