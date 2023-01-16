package com.wade.controller;

import com.wade.Stock;
import com.wade.StockQuote;
import com.wade.StockQuoteProviderGrpc.StockQuoteProviderBlockingStub;
import com.wade.StockQuoteProviderGrpc.StockQuoteProviderStub;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StockQuoteController {

  private final StockQuoteProviderBlockingStub blockingStub;
  private final StockQuoteProviderStub nonBlockingStub;

  private static final List<Stock> stocks;

   static {
    stocks = Arrays.asList(
        Stock.newBuilder().setTickerSymbol("AU").setCompanyName("Auburn Corp").setDescription("Aptitude Intel").build(),
        Stock.newBuilder().setTickerSymbol("BAS").setCompanyName("Bassel Corp").setDescription("Business Intel").build(),
        Stock.newBuilder().setTickerSymbol("COR").setCompanyName("Corvine Corp").setDescription("Corporate Intel").build(),
        Stock.newBuilder().setTickerSymbol("DIA").setCompanyName("Dialogic Corp").setDescription("Development Intel").build(),
        Stock.newBuilder().setTickerSymbol("EUS").setCompanyName("Euskaltel Corp").setDescription("English Intel").build());
  }

  @GetMapping("/stock_quote")
  public void getStockQuote(){

    Stock request = Stock.newBuilder()
        .setTickerSymbol("AU")
        .setCompanyName("Austich")
        .setDescription("server streaming example")
        .build();

    try {
      log.info("REQUEST - ticker symbol {}", request.getTickerSymbol());
      Iterator<StockQuote> stockQuotes = blockingStub.serverSideStreamingGetListStockQuotes(request);
      for (int i = 1; stockQuotes.hasNext(); i++) {
        StockQuote stockQuote = stockQuotes.next();
        log.info("RESPONSE - Price #{}: {}", i, stockQuote.getPrice());
      }
    } catch (StatusRuntimeException e) {
      log.info("RPC failed: {}", e.getStatus());
    }
  }

  @GetMapping("/stock_statistics")
  public void stockStatistics(){
    StreamObserver<StockQuote> responseObserver = new StreamObserver<StockQuote>() {
      @Override
      public void onNext(StockQuote summary) {
        log.info("RESPONSE, got stock statistics - Average Price: {}, description: {}", summary.getPrice(), summary.getDescription());
      }

      @Override
      public void onCompleted() {
        log.info("Finished clientSideStreamingGetStatisticsOfStocks");
      }

      // Override OnError ...
      @Override
      public void onError(Throwable throwable) {}
    };

    StreamObserver<Stock> requestObserver = nonBlockingStub.clientSideStreamingGetStatisticsOfStocks(responseObserver);
    try {
      for (Stock stock : stocks) {
        log.info("REQUEST: {}, {}", stock.getTickerSymbol(), stock.getCompanyName());
        requestObserver.onNext(stock);
      }
    } catch (RuntimeException e) {
      requestObserver.onError(e);
      throw e;
    }
    requestObserver.onCompleted();
  }

  @GetMapping("/bidirectional_stock_quote")
  public void bidirectionalStockQuote() throws InterruptedException {
    StreamObserver<StockQuote> responseObserver = new StreamObserver<StockQuote>() {
      @Override
      public void onNext(StockQuote stockQuote) {
        log.info("RESPONSE price#{} : {}, description:{}", stockQuote.getOfferNumber(), stockQuote.getPrice(), stockQuote.getDescription());
      }

      @Override
      public void onCompleted() {
        log.info("Finished bidirectionalStreamingGetListsStockQuotes");
      }

      //Override onError() ...
      @Override
      public void onError(Throwable throwable) {}
    };

    StreamObserver<Stock> requestObserver = nonBlockingStub.bidirectionalStreamingGetListsStockQuotes(responseObserver);
    try {
      for (Stock stock : stocks) {
        log.info("REQUEST: {}, {}", stock.getTickerSymbol(), stock.getCompanyName());
        requestObserver.onNext(stock);
        Thread.sleep(200);
      }
    } catch (RuntimeException e) {
      requestObserver.onError(e);
      throw e;
    }
    requestObserver.onCompleted();
  }

}
