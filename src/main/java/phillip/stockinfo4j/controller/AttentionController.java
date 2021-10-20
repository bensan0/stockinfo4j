package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import phillip.stockinfo4j.model.dto.AttentionReq;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.model.pojo.Attention;
import phillip.stockinfo4j.service.impl.AttentionServiceImpl;

import java.util.List;

@RestController
@RequestMapping("attention")
public class AttentionController {

    @Autowired
    AttentionServiceImpl attentionService;

    @GetMapping("all")
    public BasicRes getAll(){
        BasicRes resp = new BasicRes();
        List<Attention> result = attentionService.getAllAttention();
        resp.setData(result);
        return resp;
    }

    @PostMapping("add")
    public BasicRes add(@RequestBody AttentionReq req){
        BasicRes resp = new BasicRes();
        attentionService.addAttention(req);
        return resp;
    }

    @DeleteMapping("del")
    public BasicRes del(@RequestParam String code){
        BasicRes resp = new BasicRes();
        attentionService.delAttention(code);
        return resp;
    }

}
