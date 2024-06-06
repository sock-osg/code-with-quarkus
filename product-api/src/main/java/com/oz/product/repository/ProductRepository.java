package com.oz.product.repository;

import com.oz.product.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProductRepository {

  private final EntityManager entityManager;

  @Inject
  public ProductRepository(
      EntityManager entityManager
  ) {
    this.entityManager = entityManager;
  }

  @Transactional
  public void createProduct(Product product) {
    this.entityManager.persist(product);
  }

  @Transactional
  public void deleteProduct(Long productId) {
    final Product productEntity = this.entityManager.find(Product.class, productId);
    this.entityManager.remove(productEntity);
  }

  public List<Product> getProducts() {
    return this.entityManager.createQuery("from Product", Product.class).getResultList();
  }

  public Product getProductById(Long productId) {
    return this.entityManager.find(Product.class, productId);
  }

  @Transactional
  public void updateProduct(Product product) {
    final Product productEntity = this.entityManager.find(Product.class, product.getId());

    productEntity.setDescription(product.getDescription());
    productEntity.setName(product.getName());

    this.entityManager.merge(productEntity);
  }
}
