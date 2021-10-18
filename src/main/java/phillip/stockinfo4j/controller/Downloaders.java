package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.service.DownloadService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
    public BasicRes getStockDaily(@RequestParam("date") String yyyyMMdd) throws IOException, ExecutionException, InterruptedException {
        BasicRes resp = new BasicRes();
        DownloadUtils.isDateSaturdayOrSunday(yyyyMMdd);
        DownloadUtils.isDateConform(yyyyMMdd);
        if (!DownloadUtils.isDateSaturdayOrSunday(yyyyMMdd) || !DownloadUtils.isDateConform(yyyyMMdd)) {
            resp.builder().errorEnum(ErrorEnum.DateFormatNotAllowed).build();
        }
        downloadService.getDaily(yyyyMMdd);
        return resp;
    }

    @GetMapping("distribution")
    public BasicRes getDistribution() {
        BasicRes resp = new BasicRes();
        downloadService.getTWCCDistribution();
        return resp;
    }


    @GetMapping("/testAdvice")
    public BasicRes getEx() {
        BasicRes resp = new BasicRes();
        return resp;
    }

}