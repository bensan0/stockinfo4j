package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import phillip.stockinfo4j.model.dto.FiltStockDailyReq;
import phillip.stockinfo4j.service.impl.SearchServiceImpl;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    SearchServiceImpl searchService;

    @PostMapping("filtstockdaily")
    public FiltStockDailyReq filtStockDaily(@RequestBody FiltStockDailyReq req) {
        System.out.println(req);
        return req;
    }

    @GetMapping("/test")
    public void test() {
        searchService.test();
    }
}
