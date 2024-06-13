package com.oz.udemy.quarkus.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.udemy.quarkus.customer.entity.Customer;
import com.oz.udemy.quarkus.customer.entity.CustomerView;
import com.oz.udemy.quarkus.customer.entity.Product;
import com.oz.udemy.quarkus.customer.repository.CustomerRepository;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
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
  private final CustomerRepository customerRepository;

  @Inject
  public CustomerApi(
      final Vertx vertx,
      final CustomerRepository customerRepository
  ) {
    this.vertx = vertx;
    this.customerRepository = customerRepository;
  }

  @PostConstruct
  void init() {
    this.webClient = WebClient.create(this.vertx, new WebClientOptions()
        .setDefaultHost("localhost").setDefaultPort(8081).setSsl(false).setTrustAll(true));

    this.objectMapper = new ObjectMapper();
  }

  private Uni<Customer> getCustomerRx(Long id) {
    return Uni.createFrom().item(this.customerRepository.getCustomerById(id));
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
  public List<CustomerView> getAll() {
    return this.customerRepository.getCustomers();
  }

  @GET
  @Path("/{id}")
  public Customer getById(@PathParam("id") Long customerId) {
    return this.customerRepository.getCustomerById(customerId);
  }

  @GET
  @Path("/{id}/product")
  @Blocking
  public Uni<Customer> getProductsById(@PathParam("id") Long customerId) {
    return Uni.combine().all().unis(this.getCustomerRx(customerId), this.getAllProductsRx())
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
  public Response create(Customer product) {
    this.customerRepository.createCustomer(product);

    return Response.status(Response.Status.CREATED).build();
  }

  @PUT
  public Response update(Customer customer) {
    this.customerRepository.updateProduct(customer);

    return Response.noContent().build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") Long customerId) {
    this.customerRepository.deleteCustomer(customerId);

    return Response.noContent().build();
  }
}
