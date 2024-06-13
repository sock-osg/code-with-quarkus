package com.oz.udemy.quarkus.customer.repository;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.oz.udemy.quarkus.customer.entity.Customer;
import com.oz.udemy.quarkus.customer.entity.CustomerView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CustomerRepository {

  private final EntityManager entityManager;
  private final CriteriaBuilderFactory criteriaBuilderFactory;
  private final EntityViewManager entityViewManager;

  @Inject
  public CustomerRepository(
      final EntityManager entityManager,
      final CriteriaBuilderFactory criteriaBuilderFactory,
      final EntityViewManager entityViewManager
  ) {
    this.entityManager = entityManager;
    this.criteriaBuilderFactory = criteriaBuilderFactory;
    this.entityViewManager = entityViewManager;
  }

  public List<CustomerView> getCustomers() {
    CriteriaBuilder<Customer> criteriaBuilder = this.criteriaBuilderFactory.create(this.entityManager, Customer.class);
    return this.entityViewManager.applySetting(EntityViewSetting.create(CustomerView.class), criteriaBuilder).getResultList();
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
