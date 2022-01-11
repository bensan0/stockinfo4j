package phillip.stockinfo4j.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import phillip.stockinfo4j.aop.anno.Log;
import phillip.stockinfo4j.appconfig.BeanConfig;
import phillip.stockinfo4j.model.pojo.Stock;
import phillip.stockinfo4j.repository.StockRepo;
import phillip.stockinfo4j.service.CacheService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("啟動更新");
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

    @Component
    @Order(2)
    private class Runner2 implements ApplicationRunner {

        public Runner2() {
        }

        @Override
        @Log
        public void run(ApplicationArguments args) throws Exception {
            System.out.println("快取交易紀錄");
            LocalDate today = LocalDate.now();
            List<Integer> dates = new ArrayList<>();
            DateTimeFormatter dtf = DownloadUtils.getDateTimeFormatter("yyyyMMdd");

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
                if (dates.size() >= 10) {
                    break;
                }

                if (before.getDayOfWeek() == DayOfWeek.SATURDAY || before.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    before = before.minusDays(1);
                    continue;
                }
                dates.add(Integer.valueOf(before.format(dtf)));
                before = before.minusDays(1);
            }
            cacheService.getMultiDatesTrans(dates);
        }
    }

}
