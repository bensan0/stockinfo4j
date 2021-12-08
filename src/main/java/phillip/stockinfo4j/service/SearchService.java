package phillip.stockinfo4j.service;

import phillip.stockinfo4j.model.dto.*;

import java.util.List;

public interface SearchService {

    List<FiltStockDailyDTO> filtStockDaily(FiltStockDailyReq req);
    List<DailyTranDTO> getDaysStockAndCorp(Integer days, String code);
    List<DistributionDTO> getWeeksDistribution(Integer weeks, String code);
    List<SlowlyIncreaseDTO> getSlowlyIncrease(Integer date, Double flucPerLL, Double flucPerUL, Integer days);
    List<SlowlyIncreaseDTO> getSlowlyIncreaseTradingVol(Integer date, Double flucPerLL, Double flucPerUL, Integer days);
    List<FlucPercentDTO> getByDateAndFlucPer(Double ul, Double ll, Integer date);
    List<OverboughtRankingDTO> getOverboughtRanking(Integer date, Integer overbought);
    List<FiltStockDailyDTO> getBlackKLine(Integer date);
}
