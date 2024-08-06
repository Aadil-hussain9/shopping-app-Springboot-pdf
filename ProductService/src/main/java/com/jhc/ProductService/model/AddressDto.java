package com.jhc.ProductService.model;

import com.jhc.ProductService.entity.Address;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AddressDto {
    @NotBlank(message = "The country is required.")
    private String country;

    @NotBlank(message = "The city is required.")
    private String city;

    @NotBlank(message = "The Zip code is required.")
    @Pattern(regexp = "^\\d{1,5}$", flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE}, message = "The Zip code is invalid.")
    private String zipCode;

    @NotBlank(message = "The street name is required.")
    private String street;

    private String state;

    public Address toAddress(){
        return new Address()
                .setCountry(country)
                .setCity(city)
                .setZipCode(zipCode)
                .setStreet(street)
                .setState(state);

    }
}