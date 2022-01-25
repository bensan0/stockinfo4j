package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import phillip.stockinfo4j.Utils.OtherUtils;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.DeleteFileException;
import phillip.stockinfo4j.errorhandle.exceptions.ReadFileException;
import phillip.stockinfo4j.errorhandle.exceptions.SaveDistributionException;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.service.DownloadService;

import java.time.LocalDate;

@RestController
@RequestMapping("downloaders")
public class Downloaders {

//    private WebClient webClient = WebClient.builder().build();

    @Autowired
    DownloadService downloadService;

    /**
     * 下載每日上市櫃股票交易資訊(含法人)
     *
     * @param yyyyMMdd
     */
    @GetMapping("/daily")
    public BasicRes getStockDaily(@RequestParam("date") String yyyyMMdd) throws Exception {
        BasicRes resp = new BasicRes();
        if (yyyyMMdd == null || yyyyMMdd.length() == 0) {
            yyyyMMdd = LocalDate.now().format(OtherUtils.getDateTimeFormatter("yyyyMMdd"));
        }
        OtherUtils.isDateSaturdayOrSunday(yyyyMMdd);
        OtherUtils.isDateConform(yyyyMMdd);
        if (!OtherUtils.isDateSaturdayOrSunday(yyyyMMdd) || !OtherUtils.isDateConform(yyyyMMdd)) {
            resp.builder().errorEnum(ErrorEnum.DateFormatNotAllowed).build();
        }
        downloadService.getDaily(yyyyMMdd);
        return resp;
    }

    @GetMapping("distribution")
    public BasicRes getDistribution() throws DeleteFileException, ReadFileException, SaveDistributionException {
        BasicRes resp = new BasicRes();
        downloadService.getTWCCDistribution();
        return resp;
    }
}