package phillip.stockinfo4j.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class SearchServiceImplTest {

    @Autowired
    SearchServiceImpl service;

    @BeforeEach
    void setUp() {
        System.out.println("測試開始");
    }

    @AfterEach
    void tearDown() {
        System.out.println("測試結束");
    }

    @Test
    void filtStockDaily() {
    }

    @Test
    void getDaysStockAndCorp() {
    }

    @Test
    void getWeeksDistribution() {
    }

    @Test
    void getSlowlyIncrease() {
    }

    @Test
    void getSlowlyIncreaseTradingVol() {
    }

    @Test
    void getByDateAndFlucPer() {
    }

    @Test
    void getOverboughtRanking() {
    }

    @Test
    void getBlackKLine() {
    }
}