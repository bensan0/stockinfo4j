package phillip.stockinfo4j.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import phillip.stockinfo4j.aop.anno.Log;
import phillip.stockinfo4j.appconfig.BeanConfig;
import phillip.stockinfo4j.model.dto.DailyTranDTO;
import phillip.stockinfo4j.model.pojo.Stock;
import phillip.stockinfo4j.repository.StockRepo;
import phillip.stockinfo4j.service.CacheService;
import phillip.stockinfo4j.service.impl.CacheServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 替代@Postconstruct app啟動後自動執行
 */

@Component
public class CustomApplicationRunner {

    @Autowired
    private BeanConfig.Setting setting;

    @Autowired
    private StockRepo repo;

    @Autowired
    private CacheService cacheService;

    @PersistenceContext
    EntityManager em;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 啟動更新個股列表
     */
    @Component
    @Order(1)
    private class Runner1 implements ApplicationRunner {

        public Runner1() {
        }

        @Override
        @Log
        public void run(ApplicationArguments args) throws Exception {
            List<Stock> stockList = new ArrayList<>();
            Document docTWSE = Jsoup.connect(setting.getStockTWSEListedUrl()).get();
            Elements twseRows = docTWSE.select("tr");
            for (int i = 1; i < twseRows.size(); i++) {
                Stock stock = new Stock();
                Element row = twseRows.get(i);
                Elements cols = row.select("td");
                stock.setCode(cols.get(2).text());
                stock.setName(cols.get(3).text());
                stock.setMarket(cols.get(4).text());
                stock.setIndustry(cols.get(6).text());
                stockList.add(stock);
            }

            Document docTPEX = Jsoup.connect(setting.getStockTPEXListedUrl()).get();
            Elements tpexRows = docTPEX.select("tr");
            for (int i = 1; i < tpexRows.size(); i++) {
                Stock stock = new Stock();
                Element row = tpexRows.get(i);
                Elements cols = row.select("td");
                stock.setCode(cols.get(2).text());
                stock.setName(cols.get(3).text());
                stock.setMarket(cols.get(4).text());
                stock.setIndustry(cols.get(6).text());
                stockList.add(stock);
            }

            Document docEmerging = Jsoup.connect(setting.getStockEmergingListedUrl()).get();
            Elements emergingRows = docEmerging.select("tr");
            for (int i = 1; i < emergingRows.size(); i++) {
                Stock stock = new Stock();
                Element row = emergingRows.get(i);
                Elements cols = row.select("td");
                stock.setCode(cols.get(2).text());
                stock.setName(cols.get(3).text());
                stock.setMarket(cols.get(4).text());
                stock.setIndustry(cols.get(6).text());
                stockList.add(stock);
            }
            repo.saveAll(stockList);
        }
    }


    /**
     * 啟動時每日交易快取
     */
    @Component
    @Order(2)
    private class Runner2 implements ApplicationRunner {

        public Runner2() {
        }

        @Cacheable(value = CacheServiceImpl.DailyTranCache, key = "#root.args[0]", sync = true)
        public Map<String, DailyTranDTO> cacheDailyTran(Integer yyyyMMdd, Map<String, DailyTranDTO> dailyTran) {
            return dailyTran;
        }

        @Override
        @Log
        public void run(ApplicationArguments args) throws Exception {
            LocalDate today = LocalDate.now();
            List<Integer> dates = new ArrayList<>();
            DateTimeFormatter dtf = OtherUtils.getDateTimeFormatter("yyyyMMdd");

            //欲快取的交易日期
            while (true) {
                if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    today = today.minusDays(1);
                } else {
                    dates.add(Integer.valueOf(today.format(dtf)));
                    break;
                }
            }
            LocalDate before = today.minusDays(1);
            while (true) {
                if (dates.size() >= 22) {
                    break;
                }

                if (before.getDayOfWeek() == DayOfWeek.SATURDAY || before.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    before = before.minusDays(1);
                    continue;
                }
                dates.add(Integer.valueOf(before.format(dtf)));
                before = before.minusDays(1);
            }

            //查詢日期內交易
            List<DailyTranDTO> resultList;
            String qstr = "select ifnull(c.industry,'') as industry," +
                    "a.code , a.name, a.opening , a.highest , a.lowest , a.closing , a.deal , a.trading_vol as tradingVol , a.trading_amount as tradingAmount , a.fluctuation as fluc, a.fluc_percent as flucPer , a.per , a.`date` , a.cd_union as cdUnion ," +
                    "ifnull(b.dealer,0) as dealer ,ifnull(b.dealer_self,0) as dealerSelf ,ifnull(b.dealer_hedge,0) as dealerHedge , ifnull(b.foreign_corp,0) as foreignCorp ,ifnull(b.foreign_investors,0) as foreignInvestors ,ifnull(b.investment_trust,0) as investmentTrust ,ifnull(b.total,0) as corpTotal " +
                    "from (select * from stock_daily_trans where `date` in :dates) a " +
                    "left join corp_daily_trans b on a.cd_union = b.cd_union " +
                    "left join stock_basic_info c on c.code = a.code " +
                    "order by a.code";
            try {
                resultList = em.createNativeQuery(qstr, "DailyTranDTOResult")
                        .setParameter("dates", dates)
                        .getResultList();
            } finally {
                em.close();
            }

            //依日期分類
            Map<Integer, Map<String, DailyTranDTO>> transMap = new HashMap<>();
            resultList.stream().forEach(
                    dailyTranDTO -> {
                        if (transMap.get(dailyTranDTO.getDate()) == null) {
                            Map<String, DailyTranDTO> dateTrans = new HashMap<>();
                            dateTrans.put(dailyTranDTO.getCode(), dailyTranDTO);
                            transMap.put(dailyTranDTO.getDate(), dateTrans);
                        } else {
                            transMap.get(dailyTranDTO.getDate()).put(dailyTranDTO.getCode(), dailyTranDTO);
                        }
                    }
            );

            transMap.forEach((date, dateTrans) -> cacheDailyTran(date, dateTrans));
        }

    }

}
