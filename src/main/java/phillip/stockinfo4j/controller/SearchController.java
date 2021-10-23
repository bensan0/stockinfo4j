package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.model.dto.DistributionDTO;
import phillip.stockinfo4j.model.dto.FiltStockDailyReq;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.Distribution;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.service.impl.SearchServiceImpl;

import java.util.List;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    SearchServiceImpl searchService;

    /**
     * 篩選股票
     * @param req
     * @return
     */
    @PostMapping("filtstockdaily")
    public BasicRes filtStockDaily(@RequestBody FiltStockDailyReq req) {
        System.out.println("req:" + req);
        BasicRes resp = new BasicRes();
        DownloadUtils.isDateSaturdayOrSunday(req.getDate());
        DownloadUtils.isDateConform(req.getDate());
        resp.setData(searchService.filtStockDaily(req));
        return resp;
    }

    @GetMapping("stocktran")
    public BasicRes getDaysStock(@RequestParam(defaultValue = "5") Integer days,
                                 @RequestParam String code){
        BasicRes resp = new BasicRes();
        List<StockDailyTran> resultList = searchService.getDaysStock(days, code);
        resp.setData(resultList);
        return resp;
    }

    @GetMapping("corptran")
    public BasicRes getDaysCorp(@RequestParam(defaultValue = "5") Integer days,
                                @RequestParam String code){
        BasicRes resp = new BasicRes();
        List<CorpDailyTran> resultList = searchService.getDaysCorp(days, code);
        resp.setData(resultList);
        return resp;
    }

    @GetMapping("distribution")
    public BasicRes getWeeksDistrubution(@RequestParam Integer weeks,
                                         @RequestParam String code){
        BasicRes resp = new BasicRes();
        List<DistributionDTO> resultList = searchService.getWeeksDistribution(weeks, code);
        resp.setData(resultList);
        return resp;
    }

}
