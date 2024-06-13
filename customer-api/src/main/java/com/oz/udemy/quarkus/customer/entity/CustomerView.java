package com.oz.udemy.quarkus.customer.entity;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;

@EntityView(Customer.class)
public interface CustomerView {

  @IdMapping
  Long getId();
  String getAccountNumber();
}
