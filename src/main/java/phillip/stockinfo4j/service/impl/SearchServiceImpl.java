package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.FiltStockDailyDTO;
import phillip.stockinfo4j.model.dto.FiltStockDailyReq;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.repository.StockDailyRepo;
import phillip.stockinfo4j.repository.StockRepo;
import phillip.stockinfo4j.service.SearchService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    StockDailyRepo stockDailyRepo;

    @Autowired
    StockRepo stockRepo;

    @PersistenceContext
    EntityManager em;

    private void findStockDailyByDate(FiltStockDailyReq req) {
        //尋找輸入日期的上個交易日
        String date = req.getDate();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate today = LocalDate.parse(date, fmt);
        LocalDate yesterday = today.minusDays(1);
        Set<Integer> dates = new HashSet<>();
        while (true) {
            if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY || yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
                yesterday = yesterday.minusDays(1);
                continue;
            } else {
                break;
            }
        }
        dates.add(DownloadUtils.parseStrToInteger(today.format(fmt)));
        dates.add(DownloadUtils.parseStrToInteger(yesterday.format(fmt)));
        List<StockDailyTran> tranList = stockDailyRepo.findByDates(dates);

        //查詢今/昨所有
        Map<String, StockDailyTran> todayMap = new HashMap<>();
        Map<String, StockDailyTran> yesterdayMap = new HashMap<>();
        for (StockDailyTran tran : tranList) {
            if (tran.getDate().equals(date)) {
                todayMap.put(tran.getCode(), tran);
            } else {
                yesterdayMap.put(tran.getCode(), tran);
            }
        }

        //篩選出符合條件股的code
        //利用code查詢出DTO(含code,name,業種)

        //DTO填入今昨交易

        //回傳List<DTO>

    }

    public void test() {
        List<String> codeList = new ArrayList<>();
        codeList.add("0050");
        codeList.add("0051");
        codeList.add("6477");
        List<FiltStockDailyDTO> list = new ArrayList<>();
        Query query = null;
        String qstr = "SELECT a.code as code,a.name as name, b.name as industry FROM stock_basic_info a, industry b where a.indust_id = b.id and a.code in :codeList";
        query = em.createNativeQuery(qstr, "DTOResult");
        query.setParameter("codeList", codeList);
        List<FiltStockDailyDTO> li = query.getResultList();
        System.out.println("我肏");
        System.out.println(li);
        em.close();
    }

    public static void main(String[] args) {
        String date = "20211021";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate today = LocalDate.parse(date, fmt);
        System.out.println(today.format(fmt));
    }
}
