package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.*;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.repository.CorpDailyRepo;
import phillip.stockinfo4j.repository.DistributionRepo;
import phillip.stockinfo4j.repository.StockDailyRepo;
import phillip.stockinfo4j.service.SearchService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Integer Overbought_TRUST = 0;
    private static final Integer Overbought_FI = 1;

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
        List<Integer> dates = new ArrayList<>();
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
                yesterday = yesterday.minusDays(1);
                continue;
            }
            dates.add(DownloadUtils.parseStrToInteger(yesterday.format(fmt)));
            yesterday = yesterday.minusDays(1);
        }
        List<StockDailyTran> resultList = stockDailyRepo.findByDatesAndCode(dates, code);
        return resultList;
    }

    /***
     * 獲取天數法人交易
     * @param days
     * @param code
     * @return
     */
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
                yesterday = yesterday.minusDays(1);
                continue;
            }
            dates.add(DownloadUtils.parseStrToInteger(yesterday.format(fmt)));
            yesterday = yesterday.minusDays(1);
        }

        List<CorpDailyTran> resultList = corpDailyRepo.findByDatesAndCode(dates, code);
        return resultList;
    }

    /***
     * 股權分佈
     * @param weeks
     * @param code
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<DistributionDTO> getWeeksDistribution(Integer weeks, String code) {
        String qstr = "select a.code as code, b.name as name, a.rate11 as rate11, a.rate12 as rate12, a.rate13 as rate13, a.rate14 as rate14, a.rate15 as rate15, a.total as total, a.date as `date` " +
                "from ownership_distribution a, stock_basic_info b " +
                "where a.code = :code and a.code = b.code " +
                "order by date desc limit :weeks";
        List<DistributionDTO> resultList;

        try {
            resultList = em.createNativeQuery(qstr, "DistributionDTOResult")
                    .setParameter("code", code)
                    .setParameter("weeks", weeks)
                    .getResultList();
        } finally {
            em.close();
        }
        return resultList;
    }

    /***
     * 過去n個交易日內價格緩漲
     * @param date
     * @param flucPerLL
     * @param flucPerUL
     * @param days
     * @return
     */
    public List<SlowlyIncreaseDTO> getSlowlyIncrease(Integer date, Double flucPerLL, Double flucPerUL, Integer days) {
        List<Integer> dates = new ArrayList<>();
        DateTimeFormatter fmt = DownloadUtils.getDateTimeFormatter("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(date.toString(), fmt);
        dates.add(date);
        LocalDate pastDate = null;
        int diff = 0;
        for (int i = 1; ; i++) {
            if (diff >= days) {
                break;
            }
            pastDate = startDate.minusDays(i);
            if (pastDate.getDayOfWeek() == DayOfWeek.SATURDAY || pastDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            } else {
                diff++;
                dates.add(DownloadUtils.parseStrToInteger(pastDate.format(fmt)));
            }
        }

        List<StockDailyTran> tranList = stockDailyRepo.findByDates(dates);
        Map<String, StockDailyTran> startDateMap = new HashMap<>();
        Map<String, List<StockDailyTran>> pastDaysMap = new HashMap<>();
        for (StockDailyTran tran : tranList) {
            if (tran.getDate().toString().equals(startDate.format(fmt))) {
                startDateMap.put(tran.getCode(), tran);
            } else {
                if (pastDaysMap.get(tran.getCode()) == null) {
                    List<StockDailyTran> list = new ArrayList<>();
                    list.add(tran);
                    pastDaysMap.put(tran.getCode(), list);
                } else {
                    pastDaysMap.get(tran.getCode()).add(tran);
                }
            }
        }
        Set<String> codeList = new HashSet<>();
        Map<String, Double> pastDaysAvgPrice = new HashMap<>();
        startDateMap.forEach((code, startDateTran) -> {
            List<StockDailyTran> pastDaysTranList = pastDaysMap.get(code);
            if (pastDaysTranList == null || pastDaysTranList.isEmpty()) {
                return;
            }

            Double pastAvgPrice = 0.00;
            Double total = 0.00;
            Integer effectiveDays = 0;
            for (StockDailyTran pastDaysTran : pastDaysTranList) {
                if (pastDaysTran.getClosing() != 0.00) {
                    total += pastDaysTran.getClosing();
                    effectiveDays++;
                }
            }
            if (total != 0.00) {
                pastAvgPrice = total / effectiveDays;
            } else {
                return;
            }

            Double startDateClosing = startDateTran.getClosing();
            if (startDateClosing >= (pastAvgPrice * (1 + flucPerLL / 100)) && startDateClosing <= (pastAvgPrice * (1 + flucPerUL / 100))) {
                codeList.add(code);
                pastDaysAvgPrice.put(code, pastAvgPrice);
            }
        });
        String qstr = "SELECT a.code as code,a.name as name, b.name as industry FROM stock_basic_info a, industry b where a.indust_id = b.id and a.code in :codeList order by code";
        List<StockIndustryDTO> dtoList;
        try {
            dtoList = em.createNativeQuery(qstr, "StockDTOResult")
                    .setParameter("codeList", codeList)
                    .getResultList();
        } finally {
            em.close();
        }
        List<SlowlyIncreaseDTO> resultList = new ArrayList<>();
        DecimalFormat dfmt = DownloadUtils.getDecimalFormat();

        for (StockIndustryDTO dto : dtoList) {
            SlowlyIncreaseDTO slow = new SlowlyIncreaseDTO(dto);
            slow.setNowPrice(startDateMap.get(slow.getCode()).getClosing());
            slow.setPastPrice(Double.parseDouble(dfmt.format(pastDaysAvgPrice.get(slow.getCode()))));
            Double per = (slow.getNowPrice() - slow.getPastPrice()) / slow.getPastPrice();
            String formatted = dfmt.format(per * 100);
            slow.setFlucPercent(formatted);
            resultList.add(slow);
        }
        return resultList;
    }

    /***
     * 過去n個交易日內成交量緩漲
     * @param date
     * @param flucPerLL
     * @param flucPerUL
     * @param days
     * @return
     */
    public List<SlowlyIncreaseDTO> getSlowlyIncreaseTradingVol(Integer date, Double flucPerLL, Double flucPerUL, Integer days) {
        List<Integer> dates = new ArrayList<>();
        DateTimeFormatter fmt = DownloadUtils.getDateTimeFormatter("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(date.toString(), fmt);
        dates.add(date);
        LocalDate pastDate = null;
        int diff = 0;
        for (int i = 1; ; i++) {
            if (diff >= days) {
                break;
            }
            pastDate = startDate.minusDays(i);
            if (pastDate.getDayOfWeek() == DayOfWeek.SATURDAY || pastDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            } else {
                diff++;
                dates.add(DownloadUtils.parseStrToInteger(pastDate.format(fmt)));
            }
        }

        List<StockDailyTran> tranList = stockDailyRepo.findByDates(dates);
        Map<String, StockDailyTran> startDateMap = new HashMap<>();
        Map<String, List<StockDailyTran>> pastDaysMap = new HashMap<>();
        for (StockDailyTran tran : tranList) {
            if (tran.getDate().toString().equals(startDate.format(fmt))) {
                startDateMap.put(tran.getCode(), tran);
            } else {
                if (pastDaysMap.get(tran.getCode()) == null) {
                    List<StockDailyTran> list = new ArrayList<>();
                    list.add(tran);
                    pastDaysMap.put(tran.getCode(), list);
                } else {
                    pastDaysMap.get(tran.getCode()).add(tran);
                }
            }
        }
        Set<String> codeList = new HashSet<>();
        Map<String, Long> pastDaysAvgTradingVol = new HashMap<>();
        startDateMap.forEach((code, startDateTran) -> {
            List<StockDailyTran> pastDaysTranList = pastDaysMap.get(code);
            if (pastDaysTranList == null || pastDaysTranList.isEmpty()) {
                return;
            }

            Long pastAvgTradingVol = 0L;
            Long total = 0L;
            Integer effectiveDays = 0;
            for (StockDailyTran pastDaysTran : pastDaysTranList) {
                if (pastDaysTran.getTradingVol() != 0L) {
                    total += pastDaysTran.getTradingVol();
                    effectiveDays++;
                }
            }
            if (total != 0L) {
                pastAvgTradingVol = total / effectiveDays;
            } else {
                return;
            }

            Long startDateTradingVol = startDateTran.getTradingVol();
            if (startDateTradingVol >= (pastAvgTradingVol * (1 + flucPerLL / 100)) && startDateTradingVol <= (pastAvgTradingVol * (1 + flucPerUL / 100))) {
                codeList.add(code);
                pastDaysAvgTradingVol.put(code, pastAvgTradingVol);
            }
        });
        String qstr = "SELECT a.code as code,a.name as name, b.name as industry FROM stock_basic_info a, industry b where a.indust_id = b.id and a.code in :codeList order by code";
        List<StockIndustryDTO> dtoList;
        try {
            dtoList = em.createNativeQuery(qstr, "StockDTOResult")
                    .setParameter("codeList", codeList)
                    .getResultList();
        } finally {
            em.close();
        }
        List<SlowlyIncreaseDTO> resultList = new ArrayList<>();
        DecimalFormat dfmt = DownloadUtils.getDecimalFormat();

        for (StockIndustryDTO dto : dtoList) {
            SlowlyIncreaseDTO slow = new SlowlyIncreaseDTO(dto);
            slow.setNowTradingVol(startDateMap.get(slow.getCode()).getTradingVol());
            slow.setPastTradingVolAvg(pastDaysAvgTradingVol.get(slow.getCode()));
            Double per = (slow.getNowTradingVol().doubleValue() - slow.getPastTradingVolAvg().doubleValue()) / slow.getPastTradingVolAvg().doubleValue();
            String formatted = dfmt.format(per * 100);
            slow.setFlucPercent(formatted);
            resultList.add(slow);
        }
        return resultList;
    }

    /***
     * 漲幅查詢
     * @param ul
     * @param ll
     * @param date
     * @return
     */
    public List<FlucPercentDTO> getByDateAndFlucPer(Double ul, Double ll, Integer date) {
        List<FlucPercentDTO> resultList = new ArrayList<>();
        List<StockDailyTran> tranList = stockDailyRepo.findByflucPerAndDate(ul, ll, date);
        if (tranList == null || tranList.isEmpty()) {
            return resultList;
        }
        Map<String, StockDailyTran> tranMap = tranList.stream().collect(Collectors.toMap(tran -> tran.getCode(), tran -> tran));
        List<String> codeList = tranList.stream().map(tran -> tran.getCode()).collect(Collectors.toList());
        List<StockIndustryDTO> dtoList;
        String qstr = "SELECT a.code as code,a.name as name, b.name as industry FROM stock_basic_info a, industry b where a.indust_id = b.id and a.code in :codeList order by a.indust_id";
        try {
            dtoList = em.createNativeQuery(qstr, "StockDTOResult")
                    .setParameter("codeList", codeList)
                    .getResultList();
        } finally {
            em.close();
        }
        for (StockIndustryDTO dto : dtoList) {
            FlucPercentDTO result = new FlucPercentDTO();
            result.setCode(dto.getCode());
            result.setName(dto.getName());
            result.setIndustry(dto.getIndustry());
            StockDailyTran tran = tranMap.get(result.getCode());
            result.setClosing(tran.getClosing());
            result.setFluc(tran.getFluc());
            result.setFlucPer(tran.getFlucPer());
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 投信/外資買超排行
     *
     * @param date
     * @param overbought
     * @return
     */
    public List<OverboughtRankingDTO> getOverboughtRanking(Integer date, Integer overbought) {
        String overboughtGroup;
        if (overbought == Overbought_TRUST) {
            overboughtGroup = "investment_trust";
        } else {
            overboughtGroup = "foreign_investors";
        }
        List<OverboughtRankingDTO> resultList;
        String qstr =
                "select a.code as code, a.name as name, b.name as industry, c.closing as closing, c.fluc_percent as flucPer, d." + overboughtGroup + " as overbought, TRUNCATE(((c.trading_amount / c.trading_vol)*d." + overboughtGroup + ")/1000000,2) as tradingAmount" +
                        " from (select * from corp_daily_trans where date = :date) d" +
                        " join stock_daily_trans c on d.code = c.code and c.date =d.date" +
                        " join stock_basic_info a on d.code = a.code" +
                        " join industry b on a.indust_id = b.id" +
                        " order by tradingAmount desc limit 20";
        try {
            resultList = em.createNativeQuery(qstr, "OverboughtRankingDTOResult")
                    .setParameter("date", date)
                    .getResultList();
        } finally {
            em.close();
        }

        return resultList;
    }

    /***
     * 尋找大黑K
     * @param date
     * @return
     */
    public List<FiltStockDailyDTO> getBlackKLine(Integer date) {
        List<Integer> dates = new ArrayList<>();
        DateTimeFormatter fmt = DownloadUtils.getDateTimeFormatter("yyyyMMdd");
        LocalDate today = LocalDate.parse(date.toString(), fmt);
        dates.add(date);
        LocalDate yesterday = today.minusDays(1);
        while (true) {
            System.out.println("迴圈1");
            if (dates.size() >= 2) {
                break;
            }
            if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY || yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
                yesterday = yesterday.minusDays(1);
            } else {
                dates.add(Integer.parseInt(yesterday.format(fmt)));
            }
            continue;
        }
        System.out.println("dates:" + dates);

        List<StockDailyTran> tranList = stockDailyRepo.findByDates(dates);
        Map<String, StockDailyTran> todayMap = new HashMap<>();
        Map<String, StockDailyTran> yesterdayMap = new HashMap<>();
        for (StockDailyTran tran : tranList) {
//            System.out.println("迴圈2");
            if (tran.getDate().intValue() == date.intValue()) {
                todayMap.put(tran.getCode(), tran);
            } else {
                yesterdayMap.put(tran.getCode(), tran);
            }
        }
        System.out.println("迴圈2結束");
        System.out.println("todaysize:" + todayMap.size());
        System.out.println("yesterdaysize:" + yesterdayMap.size());

        List<String> codeList = new ArrayList<>();
        todayMap.forEach((code, tran) -> {
            System.out.println("迴圈3");
            StockDailyTran yesterdayTran = yesterdayMap.get(code);
            if (yesterdayTran == null) {
                return;
            }
            //交易量>昨日,本日收盤<本日開盤,昨漲幅>0,今漲幅<0
            if (tran.getTradingVol() > yesterdayTran.getTradingVol() &&
                    tran.getClosing() < tran.getOpening() &&
                    yesterdayTran.getFlucPer() > 2.0 &&
                    tran.getFlucPer() < 0.0) {
                codeList.add(code);
            }
        });

        List<StockIndustryDTO> dtoList;
        String qstr = "SELECT a.code as code,a.name as name, b.name as industry FROM stock_basic_info a, industry b where a.indust_id = b.id and a.code in :codeList";
        try {

            dtoList = em.createNativeQuery(qstr, "StockDTOResult").setParameter("codeList", codeList).getResultList();
        } finally {
            em.close();
        }

        List<FiltStockDailyDTO> resultList = new ArrayList<>();
        for (StockIndustryDTO dto : dtoList) {
            FiltStockDailyDTO result = new FiltStockDailyDTO(dto);
            result.getTranList().add(todayMap.get(dto.getCode()));
            result.getTranList().add(yesterdayMap.get(dto.getCode()));
            resultList.add(result);
        }
        Collections.sort(resultList, (o1, o2) -> {
            if (o1.getTranList().get(0).getTradingVol() > o2.getTranList().get(0).getTradingVol()) {
                return -1;
            } else {
                return 1;
            }
        });
        System.out.println("結束");
        return resultList;
    }
}
