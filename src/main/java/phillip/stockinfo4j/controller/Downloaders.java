package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.service.DownloadService;

import java.io.IOException;

@RestController
@RequestMapping("downloaders")
public class Downloaders {

    private WebClient webClient = WebClient.builder().build();

    @Autowired
    DownloadService downloadService;

    /**
     * 下載每日上市櫃股票交易資訊(含法人)
     *
     * @param yyyyMMdd
     */
    @GetMapping("/daily")
    public void getStockDaily(@RequestParam("date") String yyyyMMdd) {
        DownloadUtils.isDateSaturdayOrSunday(yyyyMMdd);
        DownloadUtils.isDateConform(yyyyMMdd);
        try {
            downloadService.getDaily(yyyyMMdd);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}