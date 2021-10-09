package phillip.stockinfo4j.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.SaveFailException;
import phillip.stockinfo4j.model.dto.DownloaderResponse;
import phillip.stockinfo4j.service.DownloadService;

import java.io.IOException;
import java.io.InterruptedIOException;
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
    public DownloaderResponse getStockDaily(@RequestParam("date") String yyyyMMdd) throws IOException, ExecutionException, InterruptedException,SaveFailException {
        DownloadUtils.isDateSaturdayOrSunday(yyyyMMdd);
        DownloadUtils.isDateConform(yyyyMMdd);
        DownloaderResponse resp = new DownloaderResponse();
        if (!DownloadUtils.isDateSaturdayOrSunday(yyyyMMdd)||!DownloadUtils.isDateConform(yyyyMMdd)){
            resp.setErrorMessage(ErrorEnum.DateFormatNotAllowed);
        }
        downloadService.getDaily(yyyyMMdd);
        return resp;
    }

    @GetMapping("/testAdvice")
    public DownloaderResponse getEx() throws InterruptedException {
        throw new InterruptedException("測試Inte");
    }

}