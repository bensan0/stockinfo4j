package phillip.stockinfo4j.service;

import phillip.stockinfo4j.errorhandle.exceptions.*;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DownloadService {

    void getDaily(String date) throws ExecutionException, InterruptedException, SaveCorpDailyFailedException, DeleteFileException, ReadFileException, SaveStockDailyFailedException;

    void getTWCCDistribution() throws DeleteFileException, ReadFileException, SaveDistributionException;

    CompletableFuture<List<StockDailyTran>> getTWSEStockDaily(String date) throws DeleteFileException, ReadFileException;

    CompletableFuture<List<CorpDailyTran>> getTWSECorpDaily(String date) throws DeleteFileException, ReadFileException;

    CompletableFuture<List<StockDailyTran>> getTPEXStockDaily(String date) throws DeleteFileException, ReadFileException;

    CompletableFuture<List<CorpDailyTran>> getTPEXCorpDaily(String date) throws DeleteFileException, ReadFileException;
}
