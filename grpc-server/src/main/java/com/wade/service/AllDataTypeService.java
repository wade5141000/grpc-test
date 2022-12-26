package com.wade.service;

import com.google.protobuf.Timestamp;
import com.wade.AllDataTypeRequest;
import com.wade.AllDataTypeResponse;
import com.wade.AllDataTypeServiceGrpc.AllDataTypeServiceImplBase;
import com.wade.HelloRequest;
import com.wade.annotation.GrpcService;
import io.grpc.stub.StreamObserver;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@GrpcService
@Slf4j
public class AllDataTypeService extends AllDataTypeServiceImplBase {

  @SneakyThrows
  @Override
  public void print(AllDataTypeRequest request,
      StreamObserver<AllDataTypeResponse> responseObserver) {
    log.info("{}", request);

    if (request.getField14().is(HelloRequest.class)) {
      HelloRequest field14 = request.getField14().unpack(HelloRequest.class);
      log.info("any: [{}]", field14);
    }

    Timestamp ts = request.getField15();
    LocalDateTime localDateTime = Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos())
        .atZone(ZoneId.systemDefault()).toLocalDateTime();

    log.info("timestamp: [{}]", localDateTime);

    responseObserver.onNext(AllDataTypeResponse.newBuilder().setGreeting("ok").build());
    responseObserver.onCompleted();
  }
}
