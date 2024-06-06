package com.oz;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AppLifecycleBean {

  private static final Logger logger = LoggerFactory.getLogger(AppLifecycleBean.class);

  void onStart(@Observes StartupEvent startupEvent) {
    logger.info("+++> Application starting");
  }

  void onStop(@Observes ShutdownEvent shutdownEvent) {
    logger.info("+++> Application stopping");
  }
}
