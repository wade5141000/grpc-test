package com.wade.controller;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.wade.AllDataTypeRequest;
import com.wade.AllDataTypeRequest.Inner;
import com.wade.AllDataTypeResponse;
import com.wade.AllDataTypeServiceGrpc.AllDataTypeServiceBlockingStub;
import com.wade.EnumType;
import com.wade.HelloRequest;
import com.wade.HelloResponse;
import com.wade.HelloServiceGrpc.HelloServiceBlockingStub;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {

  private final HelloServiceBlockingStub helloStub;

  private final AllDataTypeServiceBlockingStub allTypeStub;

  @GetMapping("/hello")
  public String hello() {
    HelloResponse response = helloStub.hello(HelloRequest.newBuilder()
        .setFirstName("Wade")
        .setLastName("Wu")
        .setAllType(allTypeRequest())
        .build());
    return response.getGreeting();
  }

  @GetMapping("/all-type")
  public String allType() {
    AllDataTypeResponse response = allTypeStub.print(allTypeRequest());
    return response.getGreeting();
  }

  private AllDataTypeRequest allTypeRequest() {
    Instant now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    log.info("now: [{}]", now);
    return AllDataTypeRequest.newBuilder()
        .setField1(1.3d)
        .setField2(2.6f)
        .setField3(100)
        .setField4(200L)
        .setField5(true)
        .setField6("string")
        .setField7(ByteString.copyFromUtf8("hello byte string"))
        .setField8(EnumType.E3)
        .addField9(EnumType.E2)
        .addField9(EnumType.E1)
        .addField9(EnumType.E3)
        .setField10(Inner.newBuilder().setInnerField1("inner string").build())
        .setField11(AllDataTypeResponse.newBuilder().setGreeting("greeting").build())
        .putAllField12(Map.of("Wade", Inner.newBuilder().setInnerField1("Wu").build()))
        .setOneOf3(true)
        .setOneOf1(18)
        .setOneOf2("oneof22") // oneof 只有最後設定的值才有效
        .setField14(
            Any.pack(HelloRequest.newBuilder().setFirstName("hola").setLastName("hahaha").build()))
        .setField15(
            Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
        .build();
  }

}
