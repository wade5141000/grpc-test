package com.wade.service;

import com.wade.AllDataTypeRequest;
import com.wade.AllDataTypeResponse;
import com.wade.AllDataTypeServiceGrpc.AllDataTypeServiceImplBase;
import com.wade.annotation.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@GrpcService
@Slf4j
public class AllDataTypeService extends AllDataTypeServiceImplBase {

  @Override
  public void print(AllDataTypeRequest request,
      StreamObserver<AllDataTypeResponse> responseObserver) {
    log.info("{}", request);
    responseObserver.onNext(AllDataTypeResponse.newBuilder().setGreeting("ok").build());

    responseObserver.onCompleted();
  }
}
