package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.DistributionDTO;
import phillip.stockinfo4j.model.dto.FiltStockDailyDTO;
import phillip.stockinfo4j.model.dto.FiltStockDailyReq;
import phillip.stockinfo4j.model.dto.StockIndustryDTO;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.repository.CorpDailyRepo;
import phillip.stockinfo4j.repository.DistributionRepo;
import phillip.stockinfo4j.repository.StockDailyRepo;
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
    CorpDailyRepo corpDailyRepo;

    @Autowired
    DistributionRepo distributionRepo;

    @PersistenceContext
    EntityManager em;

    /**
     * 篩選特定條件
     *
     * @param req
     * @return
     */
    public List<FiltStockDailyDTO> filtStockDaily(FiltStockDailyReq req) {
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

        //查詢今昨所有交易
        Map<String, StockDailyTran> todayMap = new HashMap<>();
        Map<String, StockDailyTran> yesterdayMap = new HashMap<>();
        for (StockDailyTran tran : tranList) {
            if (tran.getDate().toString().equals(date)) {
                todayMap.put(tran.getCode(), tran);
            } else {
                yesterdayMap.put(tran.getCode(), tran);
            }
        }

        //篩選出符合條件股的code
        List<String> codeList = new ArrayList<>();
        todayMap.forEach((code, todayTran) -> {
            StockDailyTran yesterdayTran = yesterdayMap.get(code);
            if (yesterdayTran == null) {
                return;
            }
            boolean isTVLLConfirm = todayTran.getTradingVol().doubleValue() >= yesterdayTran.getTradingVol().doubleValue() * (1.00 + req.getTradingVolFlucPercentLL());
            boolean isTVULConfirm = todayTran.getTradingVol().doubleValue() <= yesterdayTran.getTradingVol().doubleValue() * req.getTradingVolFlucPercentUL();
            boolean isYTVLLConfirm = yesterdayTran.getTradingVol() >= req.getYesterdayTradingVolLL();
            boolean isYTVULConfirm = yesterdayTran.getTradingVol() <= req.getYesterdayTradingVolUL();
            boolean isTCULConfirm = todayTran.getClosing() <= req.getTodayClosingUL();
            if (isTCULConfirm && isTVLLConfirm && isTVULConfirm && isYTVULConfirm && isYTVLLConfirm) {
                codeList.add(todayTran.getCode());
            }
        });

        //利用code查詢出DTO(含code,name,業種)
        List<StockIndustryDTO> dtoList;
        try {
            String qstr = "SELECT a.code as code,a.name as name, b.name as industry FROM stock_basic_info a, industry b where a.indust_id = b.id and a.code in :codeList";
            Query query = em.createNativeQuery(qstr, "StockDTOResult").setParameter("codeList", codeList);
            dtoList = query.getResultList();
        } finally {
            em.close();
        }

        //DTO填入今昨交易
        List<FiltStockDailyDTO> resultList = new ArrayList<>();
        for (StockIndustryDTO dto : dtoList) {
            FiltStockDailyDTO filtDTO = new FiltStockDailyDTO(dto);
            filtDTO.getTranList().add(todayMap.get(dto.getCode()));
            filtDTO.getTranList().add(yesterdayMap.get(dto.getCode()));
            resultList.add(filtDTO);
        }
        //回傳List<DTO>
        return resultList;
    }

    /**
     * 獲取天數個股交易
     *
     * @param days
     * @param code
     * @return
     */
    public List<StockDailyTran> getDaysStock(Integer days, String code) {
        DateTimeFormatter fmt = DownloadUtils.getDateTimeFormatter("yyyyMMdd");
        LocalDate today = LocalDate.now();
        Set<Integer> dates = new HashSet<>();
        dates.add(DownloadUtils.parseStrToInteger(today.format(fmt)));
        LocalDate yesterday = today.minusDays(1);
        while (true) {
            if (dates.size() == days) {
                break;
            }
            if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY || yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            dates.add(DownloadUtils.parseStrToInteger(yesterday.format(fmt)));
            yesterday = yesterday.minusDays(1);
        }

        List<StockDailyTran> resultList = stockDailyRepo.findByDatesAndCode(dates, code);
        return resultList;
    }

    public List<CorpDailyTran> getDaysCorp(Integer days, String code) {
        DateTimeFormatter fmt = DownloadUtils.getDateTimeFormatter("yyyyMMdd");
        LocalDate today = LocalDate.now();
        Set<Integer> dates = new HashSet<>();
        dates.add(DownloadUtils.parseStrToInteger(today.format(fmt)));
        LocalDate yesterday = today.minusDays(1);
        while (true) {
            if (dates.size() == days) {
                break;
            }
            if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY || yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            dates.add(DownloadUtils.parseStrToInteger(yesterday.format(fmt)));
            yesterday = yesterday.minusDays(1);
        }

        List<CorpDailyTran> resultList = corpDailyRepo.findByDatesAndCode(dates, code);
        return resultList;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<DistributionDTO> getWeeksDistribution(Integer weeks, String code) {
        String qstr = "select a.code as code, b.name as name, a.rate11 as rate11, a.rate12 as rate12, a.rate13 as rate13, a.rate14 as rate14, a.rate15 as rate15, a.total as total, a.date as `date` " +
                "from ownership_distribution a, stock_basic_info b " +
                "where a.code = :code and a.code = b.code " +
                "order by date desc limit :weeks";
        List<DistributionDTO> resultList = em.createNativeQuery(qstr, "DistributionDTOResult")
                .setParameter("code", code)
                .setParameter("weeks", weeks)
                .getResultList();
        return resultList;
    }

    public static void main(String[] args) {
        String date = "20211021";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate today = LocalDate.parse(date, fmt);
        System.out.println(today.format(fmt));
    }
}
