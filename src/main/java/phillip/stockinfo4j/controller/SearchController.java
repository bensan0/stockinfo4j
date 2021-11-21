package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.InvalidParamException;
import phillip.stockinfo4j.model.dto.*;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.service.impl.SearchServiceImpl;

import java.util.List;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    SearchServiceImpl searchService;


    /***
     * 篩選股票
     * @param req
     * @return
     */
    @PostMapping("filtstockdaily")
    public BasicRes filtStockDaily(@RequestBody FiltStockDailyReq req) {
        BasicRes resp = new BasicRes();
        DownloadUtils.isDateSaturdayOrSunday(req.getDate());
        DownloadUtils.isDateConform(req.getDate());
        resp.setData(searchService.filtStockDaily(req));
        return resp;
    }

    /***
     * 獲取個股交易資訊
     * @param days
     * @param code
     * @return
     */
    @GetMapping("stocktran")
    public BasicRes getDaysStock(@RequestParam(defaultValue = "5") Integer days,
                                 @RequestParam String code){
        if(days>366){
            throw new InvalidParamException();
        }
        BasicRes resp = new BasicRes();
        List<StockDailyTran> resultList = searchService.getDaysStock(days, code);
        resp.setData(resultList);
        return resp;
    }

    /***
     * 獲取法人交易資訊
     * @param days
     * @param code
     * @return
     */
    @GetMapping("corptran")
    public BasicRes getDaysCorp(@RequestParam(defaultValue = "5") Integer days,
                                @RequestParam String code) {
        if(days>366){
            throw new InvalidParamException();
        }
        BasicRes resp = new BasicRes();
        List<CorpDailyTran> resultList = searchService.getDaysCorp(days, code);
        resp.setData(resultList);
        return resp;
    }

    /***
     * 股權分佈
     * @param weeks
     * @param code
     * @return
     */
    @GetMapping("distribution")
    public BasicRes getWeeksDistrubution(@RequestParam(defaultValue = "4") Integer weeks,
                                         @RequestParam String code) {
        if(weeks>52){
            throw new InvalidParamException();
        }
        BasicRes resp = new BasicRes();
        List<DistributionDTO> resultList = searchService.getWeeksDistribution(weeks, code);
        resp.setData(resultList);
        return resp;
    }

    /***
     * 價格緩漲
     * @param date
     * @param flucPercentLL
     * @param flucPercentUL
     * @param days
     * @return
     */
    @GetMapping("slowlyincrease")
    public BasicRes getSlowlyIncrease(@RequestParam Integer date,
                                      @RequestParam Double flucPercentLL,
                                      @RequestParam Double flucPercentUL,
                                      @RequestParam Integer days) {
        if(days>366){
            throw new InvalidParamException();
        }
        DownloadUtils.isDateSaturdayOrSunday(date.toString());
        DownloadUtils.isDateConform(date.toString());
        List<SlowlyIncreaseDTO> resultList = searchService.getSlowlyIncrease(date, flucPercentLL, flucPercentUL, days);
        BasicRes resp = new BasicRes();
        resp.setData(resultList);
        return resp;
    }

    /***
     * 成交量緩漲
     * @param date
     * @param flucPercentLL
     * @param flucPercentUL
     * @param days
     * @return
     */
    @GetMapping("slowlyincreasetradingvol")
    public BasicRes getSlowlyIncreaseTradingVol(@RequestParam Integer date,
                                                @RequestParam Double flucPercentLL,
                                                @RequestParam Double flucPercentUL,
                                                @RequestParam Integer days) {
        if(days>366){
            throw new InvalidParamException();
        }
        DownloadUtils.isDateSaturdayOrSunday(date.toString());
        DownloadUtils.isDateConform(date.toString());
        List<SlowlyIncreaseDTO> resultList = searchService.getSlowlyIncreaseTradingVol(date, flucPercentLL, flucPercentUL, days);
        BasicRes resp = new BasicRes();
        resp.setData(resultList);
        return resp;
    }

    /***
     * 漲幅查詢
     * @param date
     * @param flucPercentLL
     * @param flucPercentUL
     * @return
     */
    @GetMapping("flucperanddate")
    public BasicRes getByDateAndFlucPer(@RequestParam Integer date,
                                        @RequestParam Double flucPercentLL,
                                        @RequestParam Double flucPercentUL) {
        DownloadUtils.isDateSaturdayOrSunday(date.toString());
        DownloadUtils.isDateConform(date.toString());
        List<FlucPercentDTO> resultList = searchService.getByDateAndFlucPer(flucPercentUL, flucPercentLL, date);
        BasicRes resp = new BasicRes();
        resp.setData(resultList);
        return resp;
    }

    /***
     * 投信/外資買超排行
     * @param date
     * @param overbought
     * @return
     */
    @GetMapping("overbought")
    public BasicRes getOverboughtRanking(@RequestParam Integer date,
                                        @RequestParam Integer overbought) {
        DownloadUtils.isDateSaturdayOrSunday(date.toString());
        DownloadUtils.isDateConform(date.toString());
        BasicRes resp = new BasicRes();
        List<OverboughtRankingDTO> resultList = searchService.getOverboughtRanking(date, overbought);
        resp.setData(resultList);
        return resp;
    }

    /***
     * 尋找大黑K
     * @param date
     * @return
     */
    @GetMapping("bigblackk")
    public BasicRes getBigBlackK(@RequestParam Integer date){
        System.out.println("date:" + date);
        DownloadUtils.isDateSaturdayOrSunday(date.toString());
        DownloadUtils.isDateConform(date.toString());
        BasicRes resp = new BasicRes();
        List<FiltStockDailyDTO> resultList = searchService.getBlackKLine(date);
        resp.setData(resultList);
        return resp;
    }
}
