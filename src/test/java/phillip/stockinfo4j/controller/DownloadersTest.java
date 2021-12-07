package phillip.stockinfo4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import phillip.stockinfo4j.model.dto.BasicRes;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Transactional
@AutoConfigureMockMvc
class DownloadersTest {

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        System.out.println("測試開始");
    }

    @AfterEach
    void tearDown() {
        System.out.println("測試結束");
    }

    @Test
    void getStockDaily() throws Exception {
        System.out.println("執行getStockDaily");
        String mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8081/downloaders/daily?date=20211007")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        BasicRes resp = new ObjectMapper().readValue(mvcResult, BasicRes.class);
        Assert.assertEquals("0000", resp.getErrorEnum().getCode());
    }

    @Test
    void getDistribution() {
        System.out.println("執行getDistribution");
    }
}