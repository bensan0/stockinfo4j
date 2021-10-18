package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.model.dto.FiltStockDailyReq;
import phillip.stockinfo4j.service.impl.SearchServiceImpl;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    SearchServiceImpl searchService;

    @PostMapping("filtstockdaily")
    public BasicRes filtStockDaily(@RequestBody FiltStockDailyReq req) {
        BasicRes resp = new BasicRes();
        DownloadUtils.isDateSaturdayOrSunday(req.getDate());
        DownloadUtils.isDateConform(req.getDate());
        resp.setData(searchService.findStockDailyByDate(req));
        return resp;
    }

    @GetMapping("/test")
    public void test() {
        searchService.test();
    }
}
