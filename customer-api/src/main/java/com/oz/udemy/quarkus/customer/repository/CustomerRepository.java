package com.oz.udemy.quarkus.customer.repository;

import com.oz.udemy.quarkus.customer.entity.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CustomerRepository {

  private final EntityManager entityManager;

  @Inject
  public CustomerRepository(
      final EntityManager entityManager
  ) {
    this.entityManager = entityManager;
  }

  public List<Customer> getCustomers() {
    return this.entityManager.createQuery("from Customer", Customer.class).getResultList();
  }

  @Transactional
  public void createCustomer(Customer customer) {
    this.entityManager.persist(customer);
  }

  @Transactional
  public void deleteCustomer(Long customerId) {
    final Customer customerEntity = this.entityManager.find(Customer.class, customerId);
    this.entityManager.remove(customerEntity);
  }

  public Customer getCustomerById(Long customerId) {
    return this.entityManager.find(Customer.class, customerId);
  }

  @Transactional
  public void updateProduct(Customer customer) {
    final Customer customerEntity = this.entityManager.find(Customer.class, customer.getId());

    customerEntity.setName(customer.getName());

    this.entityManager.merge(customerEntity);
  }
}
