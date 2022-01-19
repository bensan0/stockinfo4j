package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.service.BackupService;

@RestController
@RequestMapping("backup")
public class BackupDataController {

    @Autowired
    private BackupService backupService;

    @GetMapping("backupstockdaily")
    public BasicRes backupStockDaily(Integer startDate, Integer endDate, String fileName){
        return null;
    }

    @GetMapping("backupcorpdaily")
    public BasicRes backupCorpDaily(Integer startDate, Integer endDate, String fileName){
        return null;
    }

}
