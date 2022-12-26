package com.wade.config;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceManager {

  private static final int GRPC_SERVER_PORT = 9090;

  @SneakyThrows
  public void loadService(Map<String, Object> grpcService) {
    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(GRPC_SERVER_PORT);
    for (Object bean : grpcService.values()) {
      serverBuilder.addService((BindableService) bean);
    }
    Server server = serverBuilder.build().start();

    log.info("grpc server is started at {}", GRPC_SERVER_PORT);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      if (server != null) {
        server.shutdown();
      }
      System.err.println("*** server shut down！！！！");
    }));
    server.awaitTermination();
  }
}