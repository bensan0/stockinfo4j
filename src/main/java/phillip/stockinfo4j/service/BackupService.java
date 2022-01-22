package phillip.stockinfo4j.service;

import phillip.stockinfo4j.errorhandle.exceptions.BackupFailedException;
import phillip.stockinfo4j.model.dto.BackupDTO;

import java.io.IOException;

public interface BackupService {

    BackupDTO backupStockDaily(Integer startDate, Integer endDate, String fileName, boolean del) throws IOException, BackupFailedException;

    BackupDTO backupCorpDaily(Integer startDate, Integer endDate, String fileName, boolean del) throws IOException, BackupFailedException;
}
