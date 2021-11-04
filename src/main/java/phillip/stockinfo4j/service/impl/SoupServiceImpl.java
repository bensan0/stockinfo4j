package phillip.stockinfo4j.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import phillip.stockinfo4j.appconfig.BeanConfig;
import phillip.stockinfo4j.model.dto.StockOtherInfoDTO;
import phillip.stockinfo4j.service.SoupService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class SoupServiceImpl implements SoupService {

    @Autowired
    private BeanConfig.Setting setting;

    @Async
    public CompletableFuture<StockOtherInfoDTO> getOtherInfo(String code) throws IOException {
        StockOtherInfoDTO dto = new StockOtherInfoDTO();
        Document document = Jsoup.connect(setting.getStockOtherInfoUrl() + code).get();
        dto.setPer(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(1) > td:nth-child(1) > table > tbody > tr:nth-child(5) > td:nth-child(7)").text());
        dto.setMainBusiness(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(3) > table:nth-child(1) > tbody > tr:nth-child(15) > td > p").text());
        dto.setFinancingBalance(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(13) > div > table > tbody > tr:nth-child(2) > td:nth-child(4) > nobr").text());
        dto.setFinancingBalanceStatus(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(13) > div > table > tbody > tr:nth-child(3) > td > nobr > span").text());
        dto.setMarginBalance(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(13) > div > table > tbody > tr:nth-child(5) > td:nth-child(4) > nobr").text());
        dto.setMarginBalanceStatus(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(13) > div > table > tbody > tr:nth-child(6) > td > nobr > span").text());
        dto.setMarginFinancing(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(13) > div > table > tbody > tr:nth-child(5) > td:nth-child(7) > nobr").text().split("%")[0]);
        dto.setMarginFinancingStatus(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(13) > div > table > tbody > tr:nth-child(5) > td:nth-child(7) > nobr > div").text());
        dto.setMa5(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(6) > div > table > tbody > tr:nth-child(3) > td:nth-child(6) > nobr").text());
        dto.setMa5Bias(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(6) > div > table > tbody > tr:nth-child(3) > td:nth-child(7) > nobr").text());
        dto.setMa10(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(6) > div > table > tbody > tr:nth-child(4) > td:nth-child(6) > nobr").text());
        dto.setMa10Bias(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(6) > div > table > tbody > tr:nth-child(4) > td:nth-child(7) > nobr").text());
        dto.setMa20(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(6) > div > table > tbody > tr:nth-child(5) > td:nth-child(6) > nobr").text());
        dto.setMa20Bias(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(1) > div:nth-child(6) > div > table > tbody > tr:nth-child(5) > td:nth-child(7) > nobr").text());
        dto.setRevenueDate(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(3) > div:nth-child(13) > div > table > tbody > tr:nth-child(3) > td:nth-child(1)").text());
        dto.setRevenueMonthFluc(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(3) > div:nth-child(13) > div > table > tbody > tr:nth-child(3) > td:nth-child(3)").text());
        dto.setRevenueYearFluc(document.select("body > table:nth-child(8) > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(2) > td:nth-child(3) > div:nth-child(13) > div > table > tbody > tr:nth-child(3) > td:nth-child(4)").text());
        return CompletableFuture.completedFuture(dto);
    }
}
