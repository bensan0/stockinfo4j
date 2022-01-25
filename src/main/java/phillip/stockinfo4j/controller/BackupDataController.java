package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import phillip.stockinfo4j.Utils.OtherUtils;
import phillip.stockinfo4j.appconfig.BeanConfig;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.BackupFailedException;
import phillip.stockinfo4j.errorhandle.exceptions.InvalidParamException;
import phillip.stockinfo4j.model.dto.BackupDTO;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.service.BackupService;

import java.io.IOException;

@RestController
@RequestMapping("backup")
public class BackupDataController {

    @Autowired
    private BackupService backupService;

    @Autowired
    private BeanConfig.BackupSetting backupSetting;

    @GetMapping("backupstockdaily")
    public BasicRes backupStockDaily(Integer startDate, Integer endDate, boolean del) throws IOException, BackupFailedException, InvalidParamException {
        /*檢查起始日<結束日
        檢查起始日 結束日有效性*/
        if (endDate < startDate || !OtherUtils.isValidDate(startDate.toString(), "yyyyMMdd") || !OtherUtils.isValidDate(startDate.toString(), "yyyyMMdd")) {
            throw new InvalidParamException(ErrorEnum.DateFormatNotAllowed, "");
        }

        BasicRes resp = new BasicRes();
        BackupDTO backupDTO = backupService.backupStockDaily(startDate, endDate, backupSetting.getStockFileName(), del);
        resp.setData(backupDTO);
        return resp;
    }

    @GetMapping("backupcorpdaily")
    public BasicRes backupCorpDaily(Integer startDate, Integer endDate, boolean del) throws IOException, BackupFailedException, InvalidParamException {
        if (endDate < startDate || !OtherUtils.isValidDate(startDate.toString(), "yyyyMMdd") || !OtherUtils.isValidDate(startDate.toString(), "yyyyMMdd")) {
            throw new InvalidParamException(ErrorEnum.DateFormatNotAllowed, "");
        }

        BasicRes resp = new BasicRes();
        BackupDTO backupDTO = backupService.backupCorpDaily(startDate, endDate, backupSetting.getCorpFileName(), del);
        resp.setData(backupDTO);
        return resp;
    }

}
