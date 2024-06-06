package com.oz.udemy.quarkus.customer;

import com.oz.udemy.quarkus.customer.entity.Customer;
import com.oz.udemy.quarkus.customer.repository.CustomerRepository;
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

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerApi {

  private final CustomerRepository customerRepository;

  @Inject
  public CustomerApi(
      final CustomerRepository customerRepository
  ) {
    this.customerRepository = customerRepository;
  }

  @GET
  public List<Customer> getAll() {
    return this.customerRepository.getCustomers();
  }

  @GET
  @Path("/{id}")
  public Customer getById(@PathParam("id") Long customerId) {
    return this.customerRepository.getCustomerById(customerId);
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
