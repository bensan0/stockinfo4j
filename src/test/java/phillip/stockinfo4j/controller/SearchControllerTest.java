package phillip.stockinfo4j.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import phillip.stockinfo4j.model.dto.BasicRes;
import phillip.stockinfo4j.model.dto.FiltStockDailyReq;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Transactional
@AutoConfigureMockMvc
class SearchControllerTest {

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

    private MediaType characterJson = new MediaType("application","json", StandardCharsets.UTF_8);

    @Test
    void filtStockDaily_1() throws Exception {
        FiltStockDailyReq req = new FiltStockDailyReq();
        req.setDate("20211206");
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/search/filtstockdaily")
                        .accept(characterJson)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0000"));
    }

    @Test
    void filtStockDaily_2() throws Exception {
        FiltStockDailyReq req = new FiltStockDailyReq();
        req.setDate("202112");
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/search/filtstockdaily")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req))
        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.in(new String[] {"0004","0009"}));
    }

    @Test
    void filtStockDaily_3() throws Exception {
        FiltStockDailyReq req = new FiltStockDailyReq();
        req.setDate("文字");
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/search/filtstockdaily")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req))
        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.in(new String[] {"0004","0009"}));
    }

    @Test
    void filtStockDaily_4() throws Exception {
        FiltStockDailyReq req = new FiltStockDailyReq();
        req.setDate("20211206");
        req.setTradingVolFlucPercentLL(null);
        req.setTradingVolFlucPercentUL(null);
        req.setTodayClosingUL(null);
        req.setYesterdayTradingVolUL(null);
        req.setYesterdayTradingVolLL(null);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/search/filtstockdaily")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req))
        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0000"));
    }

    @Test
    void getDaysStock_1() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search/stocktran")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("days", "366")
                .param("code", "0000")

        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0009"));
    }

    @Test
    void getDaysStock_2() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search/stocktran")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("days", "5")
                .param("code", "0050")

        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0000"));
    }

    @Test
    void getDaysCorp_1() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search/stocktran")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("days", "32")
                .param("code", "0050")

        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0000"));
    }

    @Test
    void getDaysCorp_2() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search/stocktran")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("days", "5")
                .param("code", "0050")

        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0000"));
    }

    @Test
    void getWeeksDistrubution_1() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search/distribution")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("weeks", "5")
                .param("code", "0050")

        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0000"));
    }

    @Test
    void getWeeksDistrubution_2() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search/distribution")
                .accept(characterJson)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .param("weeks", "53")
                .param("code", "0050")

        );
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        BasicRes basicRes = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BasicRes.class);
        System.out.println(basicRes);
        MatcherAssert.assertThat(basicRes.getErrorEnum().getCode(), Matchers.comparesEqualTo("0009"));
    }
//
//    @Test
//    void getSlowlyIncrease() {
//    }
//
//    @Test
//    void getSlowlyIncreaseTradingVol() {
//    }
//
//    @Test
//    void getByDateAndFlucPer() {
//    }
//
//    @Test
//    void getOverboughtRanking() {
//    }
//
//    @Test
//    void getBigBlackK() {
//    }
}