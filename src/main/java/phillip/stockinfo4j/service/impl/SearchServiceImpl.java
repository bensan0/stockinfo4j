package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.*;
import phillip.stockinfo4j.repository.CorpDailyRepo;
import phillip.stockinfo4j.repository.DistributionRepo;
import phillip.stockinfo4j.repository.StockDailyRepo;
import phillip.stockinfo4j.service.SearchService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Integer Overbought_TRUST = 0;
    private static final Integer Overbought_FI = 1;
    private static final String Ranking = "Ranking";

    @Autowired
    StockDailyRepo stockDailyRepo;

    @Autowired
    CorpDailyRepo corpDailyRepo;

    @Autowired
    DistributionRepo distributionRepo;

    @PersistenceContext
    EntityManager em;

    @Autowired
    CacheServiceImpl cacheService;

    /***
     * 篩選特定條件
     * @param req
     * @return List<FiltStockDailyDTO>
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
        //查詢今昨所有交易
        dates.add(DownloadUtils.parseStrToInteger(today.format(fmt)));
        dates.add(DownloadUtils.parseStrToInteger(yesterday.format(fmt)));
        Map<String, Map<String, DailyTranDTO>> multiDatesTrans = cacheService.getMultiDatesTrans(dates);
        System.out.println("dates: " + dates);
        ;
        Map<String, DailyTranDTO> todayMap = multiDatesTrans.get(today.format(fmt));
        System.out.println("today: " + todayMap);
        Map<String, DailyTranDTO> yesterdayMap = multiDatesTrans.get(yesterday.format(fmt));
        System.out.println("today: " + yesterdayMap);

        //篩選出符合條件股的code
        List<FiltStockDailyDTO> resultList = new ArrayList<>();
        todayMap.forEach((code, todayDTO) -> {
            DailyTranDTO yesterdayDTO = yesterdayMap.get(code);
            if (yesterdayDTO == null) {
                return;
            }
            boolean isTVLLConfirm = todayDTO.getTradingVol().doubleValue() >= yesterdayDTO.getTradingVol().doubleValue() * (1.00 + req.getTradingVolFlucPercentLL());
            boolean isTVULConfirm = todayDTO.getTradingVol().doubleValue() <= yesterdayDTO.getTradingVol().doubleValue() * req.getTradingVolFlucPercentUL();
            boolean isYTVLLConfirm = yesterdayDTO.getTradingVol() >= req.getYesterdayTradingVolLL();
            boolean isYTVULConfirm = yesterdayDTO.getTradingVol() <= req.getYesterdayTradingVolUL();
            boolean isTCULConfirm = todayDTO.getClosing() <= req.getTodayClosingUL();
            if (isTCULConfirm && isTVLLConfirm && isTVULConfirm && isYTVULConfirm && isYTVLLConfirm) {
                FiltStockDailyDTO result = new FiltStockDailyDTO();
                result.setCode(todayDTO.getCode());
                result.setIndustry(todayDTO.getIndustry());
                result.setName(todayDTO.getName());
                result.getTranList().add(todayDTO);
                result.getTranList().add(yesterdayDTO);
                resultList.add(result);
            }
        });
        return resultList;
    }

    /***
     * 獲取天數個股+法人交易
     * @param days
     * @param code
     * @return List<DailyTranDTO>
     */
    public List<DailyTranDTO> getDaysStockAndCorp(Integer days, String code) {
        DateTimeFormatter fmt = DownloadUtils.getDateTimeFormatter("yyyyMMdd");
        LocalDate today = LocalDate.now();
        List<Integer> dates = new ArrayList<>();
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
        List<DailyTranDTO> resultList = new ArrayList<>();
        Map<String, Map<String, DailyTranDTO>> multiDatesTrans = cacheService.getMultiDatesTrans(dates);
        multiDatesTrans.forEach((date, map) -> {
            resultList.add(map.get(code));
        });
        resultList.removeAll(Collections.singleton(null));

        Collections.sort(resultList, (dto1, dto2) -> {
            if (dto1.getDate().intValue() > dto2.getDate().intValue()) {
                return -1;
            } else {
                return 1;
            }
        });
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
        List<DistributionDTO> resultList = new ArrayList<>();
        List<DistributionDTO> distributionDTOS = cacheService.cacheDistribution(code);
        if (distributionDTOS.size() < weeks) {
            weeks = distributionDTOS.size();
        }
        for (int i = 0; i < weeks; i++) {
            resultList.add(distributionDTOS.get(i));
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

        Map<String, Map<String, DailyTranDTO>> dateTranMap = cacheService.getMultiDatesTrans(dates);//{"20211010":{"0050":...,"0051":...}}
        Map<String, DailyTranDTO> startDateTranMap = dateTranMap.get(date.toString());//{"0050":...,"0051":...}

        //建立代號->過去收盤價Map
        Map<String, List<Double>> pastDaysClosingMap = new HashMap<>();
        dateTranMap.forEach((dateStr, map) -> {
            if (dateStr.equals(date.toString())) {
                return;
            }
            Set<Map.Entry<String, DailyTranDTO>> entries = map.entrySet();
            for (Map.Entry<String, DailyTranDTO> entry : entries) {
                if (pastDaysClosingMap.get(entry.getKey()) == null && entry.getValue().getClosing() > 0.0) {
                    List<Double> list = new ArrayList<>();
                    list.add(entry.getValue().getClosing());
                    pastDaysClosingMap.put(entry.getKey(), list);
                } else if (pastDaysClosingMap.get(entry.getKey()) != null && entry.getValue().getClosing() > 0.0) {
                    pastDaysClosingMap.get(entry.getKey()).add(entry.getValue().getClosing());
                }
            }
        });

        //比對本日收盤與過去均價收盤
        List<SlowlyIncreaseDTO> resultList = new ArrayList<>();
        DecimalFormat dfmt = DownloadUtils.getDecimalFormat();
        startDateTranMap.forEach((code, startDateTran) -> {
            List<Double> pastDaysClosingList = pastDaysClosingMap.get(code);
            if (pastDaysClosingList == null || pastDaysClosingList.isEmpty()) {
                return;
            }

            Double pastAvgPrice = 0.00;
            Double total = 0.00;
            Integer period = pastDaysClosingList.size();
            for (Double pastClosing : pastDaysClosingList) {
                total += pastClosing;
            }
            pastAvgPrice = total / period;

            Double startDateClosing = startDateTran.getClosing();
            if (startDateClosing >= (pastAvgPrice * (1 + flucPerLL / 100)) && startDateClosing <= (pastAvgPrice * (1 + flucPerUL / 100))) {
                SlowlyIncreaseDTO slow = new SlowlyIncreaseDTO();
                slow.setCode(startDateTran.getCode());
                slow.setName(startDateTran.getName());
                slow.setIndustry(startDateTran.getIndustry());
                slow.setNowPrice(startDateTran.getClosing());
                slow.setPastPrice(new Double(dfmt.format(pastAvgPrice)));
                Double per = (slow.getNowPrice() - slow.getPastPrice()) / slow.getPastPrice();
                String formatted = dfmt.format(per * 100);
                slow.setFlucPercent(formatted);
                resultList.add(slow);
            }
        });
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

        Map<String, Map<String, DailyTranDTO>> dateTranMap = cacheService.getMultiDatesTrans(dates);//{"20211010":{"0050":...,"0051":...}}
        Map<String, DailyTranDTO> startDateTranMap = dateTranMap.get(date.toString());//{"0050":...,"0051":...}


        //建立代號->過去交易量Map
        Map<String, List<Long>> pastDaysTradingVolMap = new HashMap<>();
        dateTranMap.forEach((dateStr, map) -> {
            if (dateStr.equals(date.toString())) {
                return;
            }
            Set<Map.Entry<String, DailyTranDTO>> entries = map.entrySet();
            for (Map.Entry<String, DailyTranDTO> entry : entries) {
                if (pastDaysTradingVolMap.get(entry.getKey()) == null && entry.getValue().getTradingVol() > 0L) {
                    List<Long> list = new ArrayList<>();
                    list.add(entry.getValue().getTradingVol());
                    pastDaysTradingVolMap.put(entry.getKey(), list);
                } else if (pastDaysTradingVolMap.get(entry.getKey()) != null && entry.getValue().getTradingVol() > 0L) {
                    pastDaysTradingVolMap.get(entry.getKey()).add(entry.getValue().getTradingVol());
                }
            }
        });

        //比對本日交易量與過去均量
        List<SlowlyIncreaseDTO> resultList = new ArrayList<>();
        DecimalFormat dfmt = DownloadUtils.getDecimalFormat();
        startDateTranMap.forEach((code, startDateTran) -> {
            List<Long> pastDaysTradingVolList = pastDaysTradingVolMap.get(code);
            if (pastDaysTradingVolList == null || pastDaysTradingVolList.isEmpty()) {
                return;
            }

            Double pastAvgTradingVol = 0.00;
            Long total = 0L;
            Integer period = pastDaysTradingVolList.size();
            for (Long pastDaysTradingVol : pastDaysTradingVolList) {
                total += pastDaysTradingVol;
            }
            pastAvgTradingVol = total * 1.0 / period;

            Long startDateTradingVol = startDateTran.getTradingVol();
            if (startDateTradingVol * 1.0 >= (pastAvgTradingVol * (1 + flucPerLL / 100)) && startDateTradingVol <= (pastAvgTradingVol * (1 + flucPerUL / 100))) {
                SlowlyIncreaseDTO slow = new SlowlyIncreaseDTO();
                slow.setCode(startDateTran.getCode());
                slow.setName(startDateTran.getName());
                slow.setIndustry(startDateTran.getIndustry());
                slow.setPastTradingVolAvg(pastAvgTradingVol.longValue());
                slow.setNowTradingVol(startDateTradingVol);
                Double per = (slow.getNowTradingVol() - pastAvgTradingVol) * 1.0 / pastAvgTradingVol;
                String formatted = dfmt.format(per * 100);
                slow.setFlucPercent(formatted);
                resultList.add(slow);
            }
        });
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
        Map<String, DailyTranDTO> dailyAllTrans = cacheService.getDailyAllTrans(date);
        if (dailyAllTrans == null || dailyAllTrans.isEmpty()) {
            return resultList;
        }
        dailyAllTrans.forEach((code, tran) -> {
            if (tran.getFlucPer() >= ll && tran.getFlucPer() <= ul) {
                FlucPercentDTO result = new FlucPercentDTO();
                result.setCode(tran.getCode());
                result.setName(tran.getName());
                result.setIndustry(tran.getIndustry());
                result.setClosing(tran.getClosing());
                result.setFluc(tran.getFluc());
                result.setFlucPer(tran.getFlucPer());
                resultList.add(result);
            }
        });
        return resultList;
    }

    /**
     * 投信/外資買超排行
     *
     * @param date
     * @param overbought
     * @return
     */
    @Cacheable(value = Ranking, key = "#root.args[1] + '_' + #root.args[0]", sync = true)
    public List<OverboughtRankingDTO> getOverboughtRanking(Integer date, Integer overbought) {
        String overboughtGroup;
        if (overbought == Overbought_TRUST) {
            overboughtGroup = "investment_trust";
        } else {
            overboughtGroup = "foreign_investors";
        }
        List<OverboughtRankingDTO> resultList;
        String qstr =
                "select a.code as code , a.name as name , c.industry as industry, b.closing as closing, b.fluc_percent as flucPer, a." + overboughtGroup + " as overbought, TRUNCATE(((b.trading_amount / b.trading_vol)*a." + overboughtGroup + ")/1000000,2) as tradingAmount " +
                        "from stockinfo4jtest.corp_daily_trans  a " +
                        "left join stockinfo4jtest.stock_daily_trans b on a.code = b.code and a.`date` = b.`date` " +
                        "left join stockinfo4jtest.stock_basic_info c on c.code = a.code " +
                        "where a.`date` = :date " +
                        "order by tradingAmount DESC " +
                        "limit 20";
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

        Map<String, Map<String, DailyTranDTO>> dateTranMap = cacheService.getMultiDatesTrans(dates);
        Map<String, DailyTranDTO> todayMap = dateTranMap.get(date.toString());
        Map<String, DailyTranDTO> yesterdayMap = dateTranMap.get(yesterday.format(fmt)) == null ? new HashMap<>() : dateTranMap.get(yesterday.format(fmt));


        List<FiltStockDailyDTO> resultList = new ArrayList<>();
        todayMap.forEach((code, tran) -> {
            DailyTranDTO yesterdayTran = yesterdayMap.get(code);
            if (yesterdayTran == null) {
                return;
            }
            //交易量>昨日,本日收盤<本日開盤,昨漲幅>0,今漲幅<0
            if (tran.getTradingVol() > yesterdayTran.getTradingVol() &&
                    tran.getClosing() < tran.getOpening() &&
                    yesterdayTran.getFlucPer() > 2.0 &&
                    tran.getFlucPer() < 0.0) {
                FiltStockDailyDTO result = new FiltStockDailyDTO();
                result.setName(tran.getName());
                result.setCode(tran.getCode());
                result.setIndustry(tran.getIndustry());
                result.getTranList().add(tran);
                result.getTranList().add(yesterdayTran);
                resultList.add(result);
            }
        });

        Collections.sort(resultList, (o1, o2) -> {
            if (o1.getTranList().get(0).getTradingVol() > o2.getTranList().get(0).getTradingVol()) {
                return -1;
            } else {
                return 1;
            }
        });
        return resultList;
    }
}
