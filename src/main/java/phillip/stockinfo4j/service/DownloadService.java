package phillip.stockinfo4j.service;

import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DownloadService {

    void getDaily(String date) throws IOException, ExecutionException, InterruptedException;

    void getTWCCDistribution();

    CompletableFuture<List<StockDailyTran>> getTWSEStockDaily(String date);

    CompletableFuture<List<CorpDailyTran>> getTWSECorpDaily(String date);

    CompletableFuture<List<StockDailyTran>> getTPEXStockDaily(String date);

    CompletableFuture<List<CorpDailyTran>> getTPEXCorpDaily(String date);
}
