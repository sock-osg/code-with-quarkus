package com.oz;

import com.oz.product.entity.Product;
import com.oz.product.repository.ProductRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductApi {

  private final ProductRepository productRepository;

  @Inject
  public ProductApi(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GET
  public List<Product> getAll() {
    return this.productRepository.getProducts();
  }

  @GET
  @Path("/{id}")
  public Product getById(@PathParam("id") Long productId) {
    return this.productRepository.getProductById(productId);
  }

  @POST
  public Response create(Product product) {
    this.productRepository.createProduct(product);

    return Response.created(URI.create(product.getId().toString())).build();
  }

  @PUT
  public Response update(Product product) {
    this.productRepository.updateProduct(product);

    return Response.noContent().build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") Long productId) {
    this.productRepository.deleteProduct(productId);

    return Response.noContent().build();
  }
}
