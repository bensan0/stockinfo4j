package phillip.stockinfo4j.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import phillip.stockinfo4j.appconfig.BeanConfig;
import phillip.stockinfo4j.model.pojo.Stock;
import phillip.stockinfo4j.repository.StockRepo;

import java.util.ArrayList;
import java.util.List;

/***
 * 替代@Postconstruct app啟動後自動執行
 */
@Component
public class CustomApplicationRunner implements ApplicationRunner {

    @Autowired
    private BeanConfig.Setting setting;

    @Autowired
    private StockRepo repo;

    @Override
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
