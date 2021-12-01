package phillip.stockinfo4j.service.impl;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import phillip.stockinfo4j.model.dto.DailyTranDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT 使用本地的一個隨機埠啟動服務
//@RunWith(SpringRunner.class) //Junit4
@ExtendWith(SpringExtension.class) //Junit5
class CacheServiceImplTest {

    @Autowired
    CacheServiceImpl service;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setUp() {
        System.out.println("測試開始");
    }

    @AfterEach
    void tearDown() {
        System.out.println("測試結束");
    }

    @Test
    void getStockDailyTrans() {
        Map<String, DailyTranDTO> dtos = service.getDailyAllTrans(20211124);
        BigInteger count = (BigInteger) em.createNativeQuery("select count(1) from stock_daily_trans where date = 20211124").getSingleResult();
        MatcherAssert.assertThat(dtos.size(), Matchers.is(count.intValue()));
    }
}