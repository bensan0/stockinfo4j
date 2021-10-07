package phillip.stockinfo4j.service;

import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DownloadService {

    void getDaily(String date) throws IOException;

    CompletableFuture<List<StockDailyTran>> getTWSEStockDaily(String date) throws IOException;

    CompletableFuture<List<CorpDailyTran>> getTWSECorpDaily(String date) throws IOException;

    CompletableFuture<List<StockDailyTran>> getTPEXStockDaily(String date) throws IOException;

    CompletableFuture<List<CorpDailyTran>> getTPEXCorpDaily(String date) throws IOException;


}
