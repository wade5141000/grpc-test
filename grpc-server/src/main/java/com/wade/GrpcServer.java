package com.wade;

import com.wade.annotation.GrpcService;
import com.wade.config.ServiceManager;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GrpcServer {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(GrpcServer.class, args);
    Map<String, Object> grpcService = context.getBeansWithAnnotation(GrpcService.class);
    ServiceManager serviceManager = context.getBean(ServiceManager.class);
    serviceManager.loadService(grpcService);
  }
}
