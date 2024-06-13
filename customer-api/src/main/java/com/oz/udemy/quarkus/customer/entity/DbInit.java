package com.oz.udemy.quarkus.customer.entity;

import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DbInit {

  private final PgPool client;
  private final boolean createSchema;

  public DbInit(
      PgPool client,
      @ConfigProperty(name = "app.schema.create", defaultValue = "true") boolean createSchema
  ) {
    this.client = client;
    this.createSchema = createSchema;
  }

  void onStart(@Observes StartupEvent event) {
    if (this.createSchema) {
      initDatabase();
    }
  }

  private void initDatabase() {
    this.client.query("DROP TABLE IF EXISTS product; DROP TABLE IF EXISTS customer;").execute()
        .flatMap(rows -> this.client.query("CREATE TABLE customer (id SERIAL PRIMARY KEY, code TEXT, accountNumber TEXT, names TEXT, surname TEXT, phone text, address TEXT)").execute())
        .flatMap(rows -> this.client.query("CREATE TABLE product (id SERIAL PRIMARY KEY,customer int8 not null, product int8 not null)").execute())
        .flatMap(rows -> this.client.query("INSERT INTO customer (code, accountNumber) VALUES ('test','seed')").execute())
        .await().indefinitely();
  }
}
