package com.wade.config;

import com.wade.AllDataTypeServiceGrpc;
import com.wade.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServiceConfig {

  @Bean
  public ManagedChannel getChannel() {
    return ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext()
        .build();
  }

  @Bean
  public HelloServiceGrpc.HelloServiceBlockingStub helloServiceStub(ManagedChannel channel) {
    return HelloServiceGrpc.newBlockingStub(channel);
  }

  @Bean
  public AllDataTypeServiceGrpc.AllDataTypeServiceBlockingStub AllDataTypeServiceStub(
      ManagedChannel channel) {
    return AllDataTypeServiceGrpc.newBlockingStub(channel);
  }

}
