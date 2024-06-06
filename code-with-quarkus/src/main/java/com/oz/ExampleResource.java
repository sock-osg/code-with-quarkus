package com.oz;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/hello")
public class ExampleResource {

  private final String greeting;

  public ExampleResource(
      @ConfigProperty(name = "greeting") final String greeting
  ) {
    this.greeting = greeting;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "Hello from Quarkus REST";
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("custom/{name}")
  public String customHello(@PathParam("name") String name) {
    return this.greeting + name + " from Quarkus REST";
  }
}
