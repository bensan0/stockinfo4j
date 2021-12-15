package phillip.stockinfo4j.service;

import phillip.stockinfo4j.model.dto.DailyTranDTO;
import phillip.stockinfo4j.model.dto.DistributionDTO;

import java.util.List;
import java.util.Map;

public interface CacheService {

    Map<String, DailyTranDTO> getDailyAllTrans(Integer yyyyMMdd);
    Map<String, DailyTranDTO> cacheLatestDownloadDailyAllTrans(Integer yyyyMMdd);
    Map<String, Map<String, DailyTranDTO>> getMultiDatesTrans(List<Integer> dates);
    List<DistributionDTO> cacheDistribution(String code);
}
