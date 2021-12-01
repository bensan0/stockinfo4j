package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.DailyTranDTO;
import phillip.stockinfo4j.model.dto.DistributionDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl {

    @PersistenceContext
    EntityManager em;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ApplicationContext applicationContext;

    private static final String DailyTranCache = "DailyTran";
    private static final String DistributionCache = "Distribution";
    private static final String Ranking = "Ranking";
    private static final Integer Overbought_TRUST = 0;
    private static final Integer Overbought_FI = 1;

    /***
     * 快取日期-交易
     * @param yyyyMMdd
     * @return Map<code, DailyTranDTO>
     */
    @Cacheable(value = DailyTranCache, key = "#root.args[0]", sync = true)
    public Map<String, DailyTranDTO> getDailyAllTrans(Integer yyyyMMdd) {
        String qstr = "select ifnull(c.industry,'') as industry," +
                "a.code , a.name, a.opening , a.highest , a.lowest , a.closing , a.deal , a.trading_vol as tradingVol , a.trading_amount as tradingAmount , a.fluctuation as fluc, a.fluc_percent as flucPer , a.per , a.`date` , a.cd_union as cdUnion ," +
                "ifnull(b.dealer,0) as dealer ,ifnull(b.dealer_self,0) as dealerSelf ,ifnull(b.dealer_hedge,0) as dealerHedge , ifnull(b.foreign_corp,0) as foreignCorp ,ifnull(b.foreign_investors,0) as foreignInvestors ,ifnull(b.investment_trust,0) as investmentTrust ,ifnull(b.total,0) as corpTotal " +
                "from (select * from stock_daily_trans where `date` = :date) a " +
                "left join corp_daily_trans b on a.code = b.code and a.`date` = b.`date` " +
                "left join stock_basic_info c on c.code = a.code " +
                "order by a.code";

        List<DailyTranDTO> resultList;
        try {
            resultList = em.createNativeQuery(qstr, "DailyTranDTOResult")
                    .setParameter("date", yyyyMMdd)
                    .getResultList();
        } finally {
            em.close();
        }

        if (resultList == null) {
            return new HashMap<>();
        }

        Map<String, DailyTranDTO> resultMap = resultList.stream()
                .collect(Collectors.toMap(dto -> dto.getCode(), dto -> dto));
        return resultMap;
    }

    /***
     * 下載每日個股時更新快取
     * @param yyyyMMdd
     * @return Map<code, DailyTranDTO>
     */
    @CachePut(value = DailyTranCache, key = "#root.args[0]")
    public Map<String, DailyTranDTO> cacheLatestDownloadDailyAllTrans(Integer yyyyMMdd) {
        String qstr = "select ifnull(c.industry,'') as industry," +
                "a.code , a.name, a.opening , a.highest , a.lowest , a.closing , a.deal , a.trading_vol as tradingVol , a.trading_amount as tradingAmount , a.fluctuation as fluc, a.fluc_percent as flucPer , a.per , a.`date` , a.cd_union as cdUnion ," +
                "ifnull(b.dealer,0) as dealer ,ifnull(b.dealer_self,0) as dealerSelf ,ifnull(b.dealer_hedge,0) as dealerHedge , ifnull(b.foreign_corp,0) as foreignCorp ,ifnull(b.foreign_investors,0) as foreignInvestors ,ifnull(b.investment_trust,0) as investmentTrust ,ifnull(b.total,0) as corpTotal " +
                "from (select * from stock_daily_trans where `date` = :date) a " +
                "left join corp_daily_trans b on a.code = b.code and a.`date` = b.`date` " +
                "left join stock_basic_info c on c.code = a.code " +
                "order by a.code";

        List<DailyTranDTO> resultList;
        try {
            resultList = em.createNativeQuery(qstr, "DailyTranDTOResult")
                    .setParameter("date", yyyyMMdd)
                    .getResultList();
        } finally {
            em.close();
        }

        if (resultList == null) {
            return new HashMap<>();
        }

        Map<String, DailyTranDTO> resultMap = resultList.stream()
                .collect(Collectors.toMap(dto -> dto.getCode(), dto -> dto));
        return resultMap;
    }

    /***
     * 獲取複數日期交易快取
     * @param dates
     * @return Map<date, Map < code, DailyTranDTO>>
     */
    public Map<String, Map<String, DailyTranDTO>> getMultiDatesTrans(List<Integer> dates) {
        Map<String, Map<String, DailyTranDTO>> resultMap = new HashMap<>();
        for (Integer date : dates) {
            Map<String, DailyTranDTO> dailyAllTrans = applicationContext.getBean(CacheServiceImpl.class).getDailyAllTrans(date);
            resultMap.put(date.toString(), dailyAllTrans);
        }
        return resultMap;
    }

    /***
     * 股權分佈快取
     * @param code
     * @return
     */
    @Cacheable(value = DistributionCache, key = "#root.args[0]", sync = true)
    public List<DistributionDTO> cacheDistribution(String code) {
        LocalDate today = LocalDate.now();
        LocalDate yearAgo = today.minusYears(1L);
        String qstr = "select a.code as code, ifnull(b.name, '') as name, ifnull(b.industry,'') as industry ,a.rate11 as rate11, a.rate12 as rate12, a.rate13 as rate13, a.rate14 as rate14, a.rate15 as rate15, a.total as total, a.date as date " +
                "from ownership_distribution a " +
                "left join stock_basic_info b on b.code = a.code " +
                "where a.code = :code and a.`date` >= :yearAgo " +
                "order by date desc";
        List<DistributionDTO> resultList;
        System.out.println(yearAgo.format(DownloadUtils.getDateTimeFormatter("yyyyMMdd")));
        try {
            resultList = em.createNativeQuery(qstr, "DistributionDTOResult")
                    .setParameter("code", code)
                    .setParameter("yearAgo", new Integer(yearAgo.format(DownloadUtils.getDateTimeFormatter("yyyyMMdd"))).intValue())
                    .getResultList();
        } finally {
            em.close();
        }
        return resultList;
    }
}
