package phillip.stockinfo4j.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import phillip.stockinfo4j.appconfig.BeanConfig;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.BackupFailedException;
import phillip.stockinfo4j.model.dto.BackupDTO;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.repository.CorpDailyRepo;
import phillip.stockinfo4j.repository.StockDailyRepo;
import phillip.stockinfo4j.service.BackupService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Transactional
class BackupServiceImplTest {

    @Autowired
    BackupService backupService;

    @Autowired
    BeanConfig.BackupSetting setting;

    @MockBean
    StockDailyRepo sdRepo;

    @MockBean
    CorpDailyRepo cdRepo;

    private final String fileName = "test_backup";

    @BeforeEach
    void setUp() {
        File backupDir = new File(setting.getFilePath() + setting.getDirName());
        for(File f:backupDir.listFiles() ){
            if(f.getName().contains(fileName)){
                f.delete();
            }
        }
        System.out.println("test start");
    }

    @AfterEach
    void tearDown() {
        File backupDir = new File(setting.getFilePath() + setting.getDirName());
        for(File f:backupDir.listFiles() ){
            if(f.getName().contains(fileName)){
                f.delete();
            }
        }
        System.out.println("test finish");
    }

    @Test
    void backupStockDaily1() throws IOException {
        List<StockDailyTran> expectedTrans = new ArrayList<>();
        for(int i = 1;i<=setting.getPerDataVol()+1;i++){
            StockDailyTran test = new StockDailyTran();
            test.setId(new Long(i));
            test.setCode("0000");
            test.setName("test");
            test.setDate(20990101);
            test.setCdUnion("20990101-0000");
            expectedTrans.add(test);
        }

        Mockito.when(sdRepo.findBetweenDate(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expectedTrans);
        Assertions.assertDoesNotThrow(()->backupService.backupStockDaily(20211201, 20211231, fileName, false));
    }

    //例外測試:查無資料
    @Test
    void backupStockDaily2() throws IOException {
        List<StockDailyTran> expectedTrans = new ArrayList<>();
        Mockito.when(sdRepo.findBetweenDate(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expectedTrans);
        BackupFailedException aThrows = Assertions.assertThrows(BackupFailedException.class, () -> backupService.backupStockDaily(20990101, 20990131, fileName, false));
        Assertions.assertEquals(ErrorEnum.NoData, aThrows.getErrorEnum());
    }

    //例外測試:檔案已存在
    @Test
    void backupStockDaily3() throws IOException {
        File testFile = new File(setting.getFilePath() + setting.getDirName() + "/" + fileName + setting.getExtensionName());
        if(!testFile.exists()){
            testFile.createNewFile();
        }
        List<StockDailyTran> expectedTrans = new ArrayList<>();
        expectedTrans.add(new StockDailyTran());
        Mockito.when(sdRepo.findBetweenDate(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expectedTrans);
        BackupFailedException aThrows = Assertions.assertThrows(BackupFailedException.class, () -> backupService.backupStockDaily(20990101, 20990131, fileName, false));
        Assertions.assertEquals(ErrorEnum.BackupFileIsExisted, aThrows.getErrorEnum());
    }

    @Test
    void backupCorpDaily1() {
        List<CorpDailyTran> expectedTrans = new ArrayList<>();
        for(int i = 1;i<=setting.getPerDataVol()+1;i++){
            CorpDailyTran test = new CorpDailyTran();
            test.setId(new Long(i));
            test.setCode("0000");
            test.setName("test");
            test.setDate(20990101);
            test.setCdUnion("20990101-0000");
            expectedTrans.add(test);
        }
        Mockito.when(cdRepo.findBetweenDate(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expectedTrans);
        Assertions.assertDoesNotThrow(()->backupService.backupCorpDaily(20990101, 20990131, fileName, false));
    }

    @Test
    void backupCorpDaily2() {
        List<CorpDailyTran> expectedTrans = new ArrayList<>();
        Mockito.when(cdRepo.findBetweenDate(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expectedTrans);
        BackupFailedException aThrows = Assertions.assertThrows(BackupFailedException.class, () -> backupService.backupCorpDaily(20990101, 20990131, fileName, false));
        Assertions.assertEquals(ErrorEnum.NoData, aThrows.getErrorEnum());
    }

    @Test
    void backupCorpDaily3() throws IOException {
        File testFile = new File(setting.getFilePath() + setting.getDirName() + "/" + fileName +"_20990101-20990131"+ setting.getExtensionName());
        if(!testFile.exists()){
            testFile.createNewFile();
        }
        List<CorpDailyTran> expectedTrans = new ArrayList<>();
        expectedTrans.add(new CorpDailyTran());
        Mockito.when(cdRepo.findBetweenDate(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expectedTrans);
        BackupFailedException aThrows = Assertions.assertThrows(BackupFailedException.class, () -> backupService.backupCorpDaily(20990101, 20990131, fileName, false));
        Assertions.assertEquals(ErrorEnum.BackupFileIsExisted, aThrows.getErrorEnum());
    }
}