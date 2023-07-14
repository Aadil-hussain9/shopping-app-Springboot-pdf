package com.jhc.ProductService.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ProductId;

    @Column(name = "PRODUCT_NAME")
    private String ProductName;
    @Column(name = "PRICE")
    private long price;

    @Column(name = "QUANTITY")
    private long quantity;
}
