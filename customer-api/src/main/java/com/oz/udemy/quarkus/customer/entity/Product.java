package com.oz.udemy.quarkus.customer.entity;

import lombok.Data;

@Data
public class Product {

    private Long id;
    private Customer customer;
    private Long product;
    private String name;
    private String code;
    private String description;
}
