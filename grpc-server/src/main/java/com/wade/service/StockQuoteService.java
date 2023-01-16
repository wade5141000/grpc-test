package com.wade.service;

import com.wade.Stock;
import com.wade.StockQuote;
import com.wade.StockQuoteProviderGrpc.StockQuoteProviderImplBase;
import com.wade.annotation.GrpcService;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;

@GrpcService
@Slf4j
public class StockQuoteService extends StockQuoteProviderImplBase {

  @Override
  public void serverSideStreamingGetListStockQuotes(Stock request,
      StreamObserver<StockQuote> responseObserver) {
    for (int i = 1; i <= 5; i++) {
      StockQuote stockQuote = StockQuote.newBuilder()
          .setPrice(fetchStockPriceBid(request))
          .setOfferNumber(i)
          .setDescription("Price for stock:" + request.getTickerSymbol())
          .build();
      responseObserver.onNext(stockQuote);
    }
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<Stock> clientSideStreamingGetStatisticsOfStocks(
      StreamObserver<StockQuote> responseObserver) {
    return new StreamObserver<Stock>() {
      int count;
      double price = 0.0;
      final StringBuffer sb = new StringBuffer();

      @Override
      public void onNext(Stock stock) {
        log.info("received stock:{}", stock.getTickerSymbol());
        count++;
        price = +fetchStockPriceBid(stock);
        sb.append(":")
            .append(stock.getTickerSymbol());
      }

      @Override
      public void onCompleted() {
        responseObserver.onNext(StockQuote.newBuilder()
            .setPrice(price / count)
            .setDescription("Statistics-" + sb)
            .build());
        responseObserver.onCompleted();
      }

      // handle onError() ...
      @Override
      public void onError(Throwable throwable) {}
    };
  }

  @Override
  public StreamObserver<Stock> bidirectionalStreamingGetListsStockQuotes(
      StreamObserver<StockQuote> responseObserver) {
    return new StreamObserver<Stock>() {
      @Override
      public void onNext(Stock request) {
        for (int i = 1; i <= 5; i++) {
          StockQuote stockQuote = StockQuote.newBuilder()
              .setPrice(fetchStockPriceBid(request))
              .setOfferNumber(i)
              .setDescription("Price for stock:" + request.getTickerSymbol())
              .build();
          responseObserver.onNext(stockQuote);
        }
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }

      //handle OnError() ...
      @Override
      public void onError(Throwable throwable) {}
    };
  }




  private static double fetchStockPriceBid(Stock stock) {
    return stock.getTickerSymbol()
        .length()
        + ThreadLocalRandom.current()
        .nextDouble(-0.1d, 0.1d);
  }
}
