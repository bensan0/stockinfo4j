package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.model.dto.StockOtherInfoDTO;
import phillip.stockinfo4j.service.SoupService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("soup")
public class SoupController {

    @Autowired
    SoupService soupService;

    @GetMapping("stockotherinfo")
    public BasicRes getOtherInfo(@RequestParam String code) throws IOException, ExecutionException, InterruptedException {
        BasicRes resp = new BasicRes();
        CompletableFuture<StockOtherInfoDTO> result = soupService.getOtherInfo(code);
        resp.setData(result.get());
        return resp;
    }
}
