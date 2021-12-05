package phillip.stockinfo4j.service.impl;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import phillip.stockinfo4j.model.dto.DailyTranDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        System.out.println("測試開始");
    }

    @AfterEach
    void tearDown() {
        System.out.println("測試結束");
    }

    @Test
    void getDailyAllTrans() {
        Map<String, DailyTranDTO> dtos = service.getDailyAllTrans(20211124);
        BigInteger count = (BigInteger) em.createNativeQuery("select count(1) from stock_daily_trans where date = 20211124").getSingleResult();
        Map<String, DailyTranDTO> redisResult = (Map<String, DailyTranDTO>)redisTemplate.opsForValue().get("DailyTran::20211124");
        MatcherAssert.assertThat(dtos.size(), Matchers.is(count.intValue()));
        MatcherAssert.assertThat(redisResult.size(), Matchers.is(count.intValue()));
    }

    @Test
    void cacheLatestDownloadDailyAllTrans() {
        Map<String, DailyTranDTO> latestDownloadDailyAllTrans = service.cacheLatestDownloadDailyAllTrans(20211124);
        Map<String, DailyTranDTO> redisResult = (Map<String, DailyTranDTO>)redisTemplate.opsForValue().get("DailyTran::20211124");
        MatcherAssert.assertThat(latestDownloadDailyAllTrans.size(), Matchers.is(redisResult.size()));
    }

    @Test
    void getMultiDatesTrans() {
        List<Integer> input = new ArrayList<>();
        input.add(20211123);
        input.add(20211122);
        Map<String, Map<String, DailyTranDTO>> multiDatesTrans = service.getMultiDatesTrans(input);
        int transSize = multiDatesTrans.get("20211122").size() + multiDatesTrans.get("20211123").size();
        BigInteger count = (BigInteger)em.createNativeQuery("select count(1) from stock_daily_trans where date in :dates")
                .setParameter("dates", input)
                .getSingleResult();
        Map<String, DailyTranDTO> redisResult1 = (Map<String, DailyTranDTO>)redisTemplate.opsForValue().get("DailyTran::20211123");
        Map<String, DailyTranDTO> redisResult2 = (Map<String, DailyTranDTO>)redisTemplate.opsForValue().get("DailyTran::20211122");
        MatcherAssert.assertThat(transSize, Matchers.is(count.intValue()));
        MatcherAssert.assertThat(transSize,Matchers.is(redisResult1.size() + redisResult2.size()));
    }

    @Test
    void cacheDistribution() {

    }
}