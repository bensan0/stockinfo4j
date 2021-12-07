package phillip.stockinfo4j.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Transactional
class DownloadServiceImplTest {

    @Autowired
    DownloadServiceImpl service;

    @BeforeEach
    void setUp() {
        System.out.println("測試開始");
    }

    @AfterEach
    void tearDown() {
        System.out.println("測試結束");
    }

    @Test
    void getDaily() {
    }

    @Test
    void getTWCCDistribution() {
    }

    @Test
    void getTWSEStockDaily() {
    }

    @Test
    void getTWSECorpDaily() {
    }

    @Test
    void getTPEXStockDaily() {
    }

    @Test
    void getTPEXCorpDaily() {
    }

    @Test
    void saveStockDaily() {
    }

    @Test
    void saveCorpDaily() {
    }

    @Test
    void saveDistribution() {
    }
}