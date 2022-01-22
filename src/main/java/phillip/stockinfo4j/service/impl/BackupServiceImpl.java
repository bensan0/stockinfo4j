package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phillip.stockinfo4j.appconfig.BeanConfig;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.BackupFailedException;
import phillip.stockinfo4j.model.dto.BackupDTO;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.repository.CorpDailyRepo;
import phillip.stockinfo4j.repository.StockDailyRepo;
import phillip.stockinfo4j.service.BackupService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class BackupServiceImpl implements BackupService {

    @Autowired
    private StockDailyRepo sdRepo;

    @Autowired
    private CorpDailyRepo cdRepo;

    @Autowired
    private BeanConfig.BackupSetting backupSetting;


    @Override
    public BackupDTO backupStockDaily(Integer startDate, Integer endDate, String fileName, boolean del) throws IOException, BackupFailedException {
        List<StockDailyTran> stockDailyTrans = sdRepo.findBetweenDate(startDate, endDate);
        if (stockDailyTrans.isEmpty()) {
            throw new BackupFailedException(ErrorEnum.NoData, "");
        }
        fileName = fileName + "_" + startDate + "-" + endDate + backupSetting.getExtensionName();
        File file;
        FileWriter fw;
        BufferedWriter bw = null;
        BackupDTO.BackupDTOBuilder dtoBuilder = BackupDTO.builder();
        BackupDTO dto;
        try {
            File backupDir = new File(backupSetting.getFilePath() + backupSetting.getDirName());
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }
            file = new File(backupDir.getPath() + "/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                throw new BackupFailedException(ErrorEnum.BackupFileIsExisted, "");
            }

            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            StringBuilder sb = new StringBuilder();
            String sql = "INSERT INTO stock_daily_trans (id,code,name,trading_vol,deal,opening,highest,lowest,closing,fluc_percent,fluctuation,per,`date`,cd_union,trading_amount) VALUES ";
            sb.append(sql).append(System.lineSeparator());
            String content = "(%d,%s,%s,%d,%d,%f,%f,%f,%f,%f,%f,%f,%d,%s,%d)";
            for (int i = 1; i <= stockDailyTrans.size(); i++) {
                StockDailyTran tran = stockDailyTrans.get(i - 1);
                sb.append(String.format(content, tran.getId(), tran.getCode(), tran.getName(), tran.getTradingVol(), tran.getDeal(), tran.getOpening(), tran.getHighest(), tran.getLowest(), tran.getClosing(), tran.getFlucPer(), tran.getFluc(), tran.getPer(), tran.getDate(), tran.getCdUnion(), tran.getTradingAmount()));
                if (i % backupSetting.getPerDataVol() == 0 && i != stockDailyTrans.size()) {
                    sb.append(";").append(System.lineSeparator()).append(sql).append(System.lineSeparator());
                } else if (i == stockDailyTrans.size()) {
                    sb.append(";");
                } else {
                    sb.append(",").append(System.lineSeparator());
                }
            }

            bw.write(sb.toString());
            bw.flush();
            bw.close();
            if (del) {
                sdRepo.delBetweenDate(startDate, endDate);
            }
            dto = dtoBuilder.backupDir(backupSetting.getDirName()).dataVol(new Long(stockDailyTrans.size())).fileName(fileName).fileSize(file.length() / 1024).isDel(del).build();

        } catch (IOException e) {
            throw new BackupFailedException(ErrorEnum.BackupFailed, e.getMessage());
        } finally {
            if (bw != null) {
                bw.close();
            }

        }
        return dto;
    }

    @Override
    public BackupDTO backupCorpDaily(Integer startDate, Integer endDate, String fileName, boolean del) throws IOException, BackupFailedException {
        List<CorpDailyTran> corpDailyTrans = cdRepo.findBetweenDate(startDate, endDate);
        if (corpDailyTrans.isEmpty()) {
            throw new BackupFailedException(ErrorEnum.NoData, "");
        }
        fileName = fileName + "_" + startDate + "-" + endDate + backupSetting.getExtensionName();
        File file;
        FileWriter fw;
        BufferedWriter bw = null;
        BackupDTO.BackupDTOBuilder dtoBuilder = BackupDTO.builder();
        BackupDTO dto;
        try {
            File backupDir = new File(backupSetting.getFilePath() + backupSetting.getDirName());
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }
            file = new File(backupDir.getPath() + "/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                throw new BackupFailedException(ErrorEnum.BackupFileIsExisted, "");
            }

            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            StringBuilder sb = new StringBuilder();
            String sql = "INSERT INTO corp_daily_trans (id,code,name,dealer,dealer_self,dealer_hedge,foreign_investors,foreign_corp,investment_trust,total,`date`,cd_union) VALUES ";
            sb.append(sql).append(System.lineSeparator());
            String content = "(%d,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%s)";
            for (int i = 1; i <= corpDailyTrans.size(); i++) {
                CorpDailyTran tran = corpDailyTrans.get(i - 1);
                sb.append(String.format(content, tran.getId(), tran.getCode(), tran.getName(), tran.getDealer(), tran.getDealerSelf(), tran.getDealerHedge(), tran.getForeignInvestors(), tran.getForeignCorp(), tran.getInvestmentTrust(), tran.getTotal(), tran.getDate(), tran.getCdUnion()));
                if (i % backupSetting.getPerDataVol() == 0 && i != corpDailyTrans.size()) {
                    sb.append(";").append(System.lineSeparator()).append(sql).append(System.lineSeparator());
                } else if (i == corpDailyTrans.size()) {
                    sb.append(";");
                } else {
                    sb.append(",").append(System.lineSeparator());
                }
            }

            bw.write(sb.toString());
            bw.flush();
            bw.close();
            if (del) {
                cdRepo.delBetweenDate(startDate, endDate);
            }
            dto = dtoBuilder.backupDir(backupSetting.getDirName()).dataVol(new Long(corpDailyTrans.size())).fileName(fileName).fileSize(file.length() / 1024).isDel(del).build();

        } catch (IOException e) {
            throw new BackupFailedException(ErrorEnum.BackupFailed, e.getMessage());
        } finally {
            if (bw != null) {
                bw.close();
            }

        }
        return dto;
    }
}
