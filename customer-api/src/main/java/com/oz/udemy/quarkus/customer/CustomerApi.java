package com.oz.udemy.quarkus.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.udemy.quarkus.customer.entity.Customer;
import com.oz.udemy.quarkus.customer.entity.Product;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.annotation.PostConstruct;
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
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerApi {

  private WebClient webClient;
  private ObjectMapper objectMapper;

  private final Vertx vertx;
  private final PgPool client;

  @Inject
  public CustomerApi(
      final Vertx vertx,
      final PgPool client
  ) {
    this.vertx = vertx;
    this.client = client;
  }

  @PostConstruct
  void init() {
    this.webClient = WebClient.create(this.vertx, new WebClientOptions()
        .setDefaultHost("localhost").setDefaultPort(8081).setSsl(false).setTrustAll(true));

    this.objectMapper = new ObjectMapper();
  }

  private Uni<List<Product>> getAllProductsRx() {
    return this.webClient.get("/product").send()
      .onFailure().invoke(error -> log.error("Error getting products", error))
      .onItem().transform(response ->
        response.bodyAsJsonArray().stream().map(rawProduct -> {
          try {
            return this.objectMapper.readValue(rawProduct.toString(), Product.class);
          } catch (JsonProcessingException jsonProcExc) {
            log.error("Error casting current product", jsonProcExc);
            return null;
          }
        })
        .filter(Objects::nonNull)
        .toList()
      );
  }

  @GET
  public Multi<Customer> getAll() {
    return Customer.findAll(this.client);
  }

  @GET
  @Path("/{id}")
  public Uni<Customer> getById(@PathParam("id") Long customerId) {
    return Customer.findById(this.client, customerId);
  }

  @GET
  @Path("/{id}/product")
  public Uni<Customer> getProductsById(@PathParam("id") Long customerId) {
    return Uni.combine().all().unis(Customer.findById(this.client, customerId), this.getAllProductsRx())
        .with((customer, fullProducts) -> {
          customer.getProducts().forEach(product -> fullProducts.stream().filter(fullProduct -> product.getId().equals(fullProduct.getId())).findFirst()
              .ifPresent(productFound -> {
                product.setName(productFound.getName());
                product.setDescription(productFound.getDescription());
              }));

          return customer;
        });
  }

  @POST
  public Uni<Response> create(Customer customer) {
    return customer.save(this.client).onItem().transform(id -> Response.status(Response.Status.CREATED).build());
  }

  @PUT
  public Uni<Response> update(Customer customer) {
    return customer.save(this.client).onItem().transform(id -> Response.noContent().build());
  }

  @DELETE
  @Path("/{id}")
  public Uni<Response> delete(@PathParam("id") Long customerId) {
    return Customer.delete(this.client, customerId).onItem().transform(id -> Response.noContent().build());
  }
}
