package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.errorhandle.exceptions.SaveCorpDailyFailedException;
import phillip.stockinfo4j.errorhandle.exceptions.SaveStockDailyFailedException;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.Distribution;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.repository.CorpDailyRepo;
import phillip.stockinfo4j.repository.DistributionRepo;
import phillip.stockinfo4j.repository.StockDailyRepo;
import phillip.stockinfo4j.service.DownloadService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private ResourceBundle resource;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockDailyRepo stockDailyRepo;

    @Autowired
    private CorpDailyRepo corpDailyRepo;

    @Autowired
    private DistributionRepo distributionRepo;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 下載並儲存所有每日
     *
     * @param date
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Async
//    @Transactional Async下標註@Transactional無效,需要在內部呼叫的方法上標註@Transactional
    public void getDaily(String date) throws ExecutionException, InterruptedException {
        List<StockDailyTran> stockDailyTrans = new LinkedList<>();
        List<CorpDailyTran> corpDailyTrans = new LinkedList<>();
        CompletableFuture<List<StockDailyTran>> twseStockDailyFuture;
        CompletableFuture<List<CorpDailyTran>> twseCorpDailyFuture;
        CompletableFuture<List<StockDailyTran>> tpexStockDailyFuture;
        CompletableFuture<List<CorpDailyTran>> tpexCorpDailyFuture;
        twseStockDailyFuture = applicationContext.getBean(DownloadService.class).getTWSEStockDaily(date);
        twseCorpDailyFuture = applicationContext.getBean(DownloadService.class).getTWSECorpDaily(date);
        tpexStockDailyFuture = applicationContext.getBean(DownloadService.class).getTPEXStockDaily(date);
        tpexCorpDailyFuture = applicationContext.getBean(DownloadService.class).getTPEXCorpDaily(date);
        try {
            stockDailyTrans.addAll(twseStockDailyFuture.get());
            stockDailyTrans.addAll(tpexStockDailyFuture.get());
            corpDailyTrans.addAll(twseCorpDailyFuture.get());
            corpDailyTrans.addAll(tpexCorpDailyFuture.get());
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
        saveStockDaily(stockDailyTrans);
        saveCorpDaily(corpDailyTrans);
    }

    /**
     * 下載並儲存當周股權分佈
     *
     * @return
     */
    public void getTWCCDistribution() {
        String filePath = downloadDistribution();
        List<Distribution> distributionList = filtDistribution(filePath);
        saveDistribution(distributionList);
    }

    /**
     * @param date
     */
    @Async
    public CompletableFuture<List<StockDailyTran>> getTWSEStockDaily(String date) {
        String filePath = downloadTWSEStockDaily(date);
        List<StockDailyTran> tranList = filtTWSEStockDaily(filePath);
        return CompletableFuture.completedFuture(tranList);
    }

    /**
     * @param date
     */
    @Async
    public CompletableFuture<List<CorpDailyTran>> getTWSECorpDaily(String date) {
        String filePath = downloadTWSECorpDaily(date);
        List<CorpDailyTran> tranList = filtTWSECorpDaily(filePath);
        return CompletableFuture.completedFuture(tranList);
    }

    /**
     * @param date
     */
    @Async
    public CompletableFuture<List<StockDailyTran>> getTPEXStockDaily(String date) {
        String filePath = downloadTPEXStockDaily(date);
        List<StockDailyTran> tranList = filtTPEXStockDaily(filePath);
        return CompletableFuture.completedFuture(tranList);
    }

    /**
     * @param date
     */
    @Async
    public CompletableFuture<List<CorpDailyTran>> getTPEXCorpDaily(String date) {
        String filePath = downloadTPEXCorpDaily(date);
        List<CorpDailyTran> tranList = filtTPEXCorpDaily(filePath);
        return CompletableFuture.completedFuture(tranList);
    }


    /**
     * 下載上市股每日交易資訊
     *
     * @param date
     * @return
     */
    private String downloadTWSEStockDaily(String date) {
        String targetDir = resource.getString("TempDir");
        String fileName = "TWSEStockDaily" + date + ".csv";
        String url = resource.getString("TWSEStockDailyUrl") + date;

        //定義請求頭的接收類型
        RequestCallback requestCallback = request -> {
            request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            request.getHeaders().setAcceptCharset(Arrays.asList(Charset.forName("big5")));
        };
        // getForObject會將所有返回直接放到內存中,使用流來替代這個操作
        ResponseExtractor<Void> responseExtractor = response -> {
            // Here I write the response to a file but do what you like
            Path path = Paths.get(targetDir);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            return null;
        };
        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        return targetDir + "/" + fileName;
    }

    /**
     * 上市股每日交易資訊轉為pojo
     *
     * @param filePath
     * @throws IOException
     */
    private List<StockDailyTran> filtTWSEStockDaily(String filePath) {
        List<StockDailyTran> tranList = new ArrayList<>();
        String content = DownloadUtils.readFileToString(filePath, "Big5");
        if (content.length() == 0) {
            return tranList;
        }
        String[] split = content.split("\"本益比\",");
        Scanner sc = new Scanner(split[1]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        DecimalFormat df = DownloadUtils.getDecimalFormat();
        try {
            while (sc.hasNext()) {
                StockDailyTran tran = new StockDailyTran();
                content = sc.next();
                if (content.startsWith("=")) {
                    content = content.substring(2, content.length() - 2);
                } else {
                    content = content.substring(1, content.length() - 2);
                }
                String[] split1 = content.split("\",\"");
                if (split1[0].trim().length() > 4) {
                    continue;
                }
                tran.setCode(split1[0].trim());
                tran.setName(split1[1].trim());
                tran.setTradingVol(DownloadUtils.parseStrToDouble(split1[2].replace(",", "").trim()).intValue() / 1000);
                tran.setDeal(DownloadUtils.parseStrToDouble(split1[3].replace(",", "").trim()).intValue());
                tran.setOpening(DownloadUtils.parseStrToDouble(split1[5].replace(",", "").trim()));
                tran.setHighest(DownloadUtils.parseStrToDouble(split1[6].replace(",", "").trim()));
                tran.setLowest(DownloadUtils.parseStrToDouble(split1[7].replace(",", "").trim()));
                tran.setClosing(DownloadUtils.parseStrToDouble(split1[8].replace(",", "").trim()));
                if (split1[9].equals("+") || split1[9].equals("-")) {
                    tran.setFluc(DownloadUtils.parseStrToDouble(split1[9].trim() + split1[10].replace(",", "").trim()));
                    Double yesterdayClosing = tran.getClosing() - tran.getFluc();
                    Double flucPer = Double.parseDouble(df.format((tran.getFluc() * 100 / yesterdayClosing)));
                    tran.setFlucPer(flucPer);
                } else {
                    tran.setFluc(0.00);
                    tran.setFlucPer(0.00);
                }
                int li = filePath.lastIndexOf("/");
                tran.setDate(Integer.parseInt(filePath.substring(li + 1 + 14, li + 1 + 22)));
                tran.setCdUnion(tran.getCode() + "-" + tran.getDate());
                tran.setPer(DownloadUtils.parseStrToDouble(split1[15]));
                tranList.add(tran);
            }
        } finally {
            sc.close();
            DownloadUtils.deleteFile(filePath);
        }
        return tranList;
    }

    /**
     * 下載上市股每日法人交易資訊
     *
     * @param date
     * @return
     */
    private String downloadTWSECorpDaily(String date) {
        String targetDir = resource.getString("TempDir");
        String fileName = "TWSECorpDaily" + date + ".csv";
        String url = resource.getString("TWSECorpDailyUrl") + date;

        //定義請求頭的接收類型
        RequestCallback requestCallback = request -> {
            request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            request.getHeaders().setAcceptCharset(Arrays.asList(Charset.forName("big5")));
        };
        // getForObject會將所有返回直接放到內存中,使用流來替代這個操作
        ResponseExtractor<Void> responseExtractor = response -> {
            // Here I write the response to a file but do what you like
            Path path = Paths.get(targetDir);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            return null;
        };
        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        return targetDir + "/" + fileName;
    }

    /**
     * 轉換上市三大法人交易資訊為Pojo
     *
     * @param filePath
     * @return
     */
    private List<CorpDailyTran> filtTWSECorpDaily(String filePath) {
        List<CorpDailyTran> tranList = new ArrayList<>();
        String content = DownloadUtils.readFileToString(filePath, "Big5");
        if (content.length() == 0) {
            return tranList;
        }
        String[] split = content.split("\"三大法人買賣超股數\",");
        split = split[1].split("\"說明:\"");
        Scanner sc = new Scanner(split[0]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        try {
            while (sc.hasNext()) {
                CorpDailyTran tran = new CorpDailyTran();
                content = sc.next();
                if (content.startsWith("=")) {
                    content = content.substring(2, content.length() - 2);
                } else {
                    content = content.substring(1, content.length() - 2);
                }
                String[] split1 = content.split("\",\"");
                if (split1[0].trim().length() > 4) {
                    continue;
                }
                tran.setCode(split1[0].trim());
                tran.setName(split1[1].trim());
                tran.setForeignInvestors(DownloadUtils.parseStrToInteger(split1[4].trim().replace(",", "")) / 1000);
                tran.setForeignCorp(DownloadUtils.parseStrToInteger(split1[7].trim().replace(",", "")) / 1000);
                tran.setInvestmentTrust(DownloadUtils.parseStrToInteger(split1[10].trim().replace(",", "")) / 1000);
                tran.setDealer(DownloadUtils.parseStrToInteger(split1[11].trim().replace(",", "")) / 1000);
                tran.setDealerSelf(DownloadUtils.parseStrToInteger(split1[14].trim().replace(",", "")) / 1000);
                tran.setDealerHedge(DownloadUtils.parseStrToInteger(split1[17].trim().replace(",", "")) / 1000);
                tran.setTotal(DownloadUtils.parseStrToInteger(split1[18].trim().replace(",", "")) / 1000);
                int li = filePath.lastIndexOf("/");
                tran.setDate(Integer.parseInt(filePath.substring(li + 1 + 13, li + 1 + 21)));
                tran.setCdUnion(tran.getCode() + "-" + tran.getDate());
                tranList.add(tran);
            }
        } finally {
            sc.close();
            DownloadUtils.deleteFile(filePath);
        }
        return tranList;
    }

    /**
     * 下載上櫃每日股票交易
     *
     * @param date
     * @return
     */
    private String downloadTPEXStockDaily(String date) {
        String targetDir = resource.getString("TempDir");
        String fileName = "TPEXStockDaily" + date + ".csv";
        date = Integer.parseInt(date.substring(0, 4)) - 1911 + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
        String url = resource.getString("TPEXStockDailyUrl") + date;

        //定義請求頭的接收類型
        RequestCallback requestCallback = request -> {
            request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            request.getHeaders().setAcceptCharset(Arrays.asList(Charset.forName("big5")));
        };
        // getForObject會將所有返回直接放到內存中,使用流來替代這個操作
        ResponseExtractor<Void> responseExtractor = response -> {
            // Here I write the response to a file but do what you like
            Path path = Paths.get(targetDir);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            return null;
        };
        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        return targetDir + "/" + fileName;
    }

    /**
     * 轉換上櫃每日股票交易資訊為Pojo
     *
     * @param filePath
     * @return
     */
    private List<StockDailyTran> filtTPEXStockDaily(String filePath) {
        List<StockDailyTran> tranList = new ArrayList<>();
        String content = DownloadUtils.readFileToString(filePath, "Big5");
        if (content.length() == 0) {
            return tranList;
        }
        String[] split = content.split("次日跌停價");
        split = split[1].split("管理股票");

        Scanner sc = new Scanner(split[0]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        DecimalFormat df = DownloadUtils.getDecimalFormat();
        try {
            while (sc.hasNext()) {
                StockDailyTran tran = new StockDailyTran();
                content = sc.next();
                if (content.length() == 0) {
                    continue;
                }
                if (content.startsWith("=")) {
                    content = content.substring(2, content.length() - 1);
                } else {
                    content = content.substring(1, content.length() - 1);
                }
                String[] split1 = content.split("\",\"");
                if (split1[0].trim().length() > 4) {
                    continue;
                }
                tran.setCode(split1[0].trim());
                tran.setName(split1[1].trim());
                tran.setClosing(DownloadUtils.parseStrToDouble(split1[2].trim().replace(",", "")));
                tran.setFluc(DownloadUtils.parseStrToDouble(split1[3].trim().replace(",", "")));
                tran.setOpening(DownloadUtils.parseStrToDouble(split1[4].trim().replace(",", "")));
                tran.setHighest(DownloadUtils.parseStrToDouble(split1[5].trim().replace(",", "")));
                tran.setLowest(DownloadUtils.parseStrToDouble(split1[6].trim().replace(",", "")));
                tran.setTradingVol(DownloadUtils.parseStrToInteger(split1[8].trim().replace(",", "")) / 1000);
                tran.setDeal(DownloadUtils.parseStrToInteger(split1[10].trim().replace(",", "")));
                Double yesterdayClosing = tran.getClosing();
                tran.setFlucPer(DownloadUtils.parseStrToDouble(df.format(tran.getFluc() * 100 / yesterdayClosing)));
                int li = filePath.lastIndexOf("/");
                tran.setDate(DownloadUtils.parseStrToInteger(filePath.substring(li + 1 + 14, li + 1 + 22)));
                tran.setCdUnion(tran.getCode() + "-" + tran.getDate());
                tranList.add(tran);
            }
        } finally {
            sc.close();
            DownloadUtils.deleteFile(filePath);
        }
        return tranList;
    }

    /**
     * 下載上櫃每日法人交易資訊
     *
     * @param date
     * @return
     */
    private String downloadTPEXCorpDaily(String date) {
        String targetDir = resource.getString("TempDir");
        String fileName = "TPEXCorpDaily" + date + ".csv";
        date = Integer.parseInt(date.substring(0, 4)) - 1911 + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
        String url = resource.getString("TPEXCorpDailyUrl") + date;

        //定義請求頭的接收類型
        RequestCallback requestCallback = request -> {
            request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        };
        // getForObject會將所有返回直接放到內存中,使用流來替代這個操作
        ResponseExtractor<Void> responseExtractor = response -> {
            // Here I write the response to a file but do what you like
            Path path = Paths.get(targetDir);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            return null;
        };
        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        return targetDir + "/" + fileName;
    }

    /**
     * 轉換上櫃每日法人交易為pojo
     *
     * @param filePath
     * @return
     */
    private List<CorpDailyTran> filtTPEXCorpDaily(String filePath) {
        List<CorpDailyTran> tranList = new LinkedList<>();
        String content = DownloadUtils.readFileToString(filePath, "big5");
        if (content.length() == 0) {
            return tranList;
        }
        String[] split = content.split("三大法人買賣超股數合計");
        Scanner sc = new Scanner(split[1]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        try {
            while (sc.hasNext()) {
                CorpDailyTran tran = new CorpDailyTran();
                content = sc.next();
                if (content.length() == 0) {
                    continue;
                }
                if (content.startsWith("=")) {
                    content = content.substring(2, content.length() - 1);
                } else {
                    content = content.substring(1, content.length() - 1);
                }
                String[] split1 = content.split("\",\"");
                if (split1[0].trim().length() > 4) {
                    continue;
                }
                tran.setCode(split1[0].trim());
                tran.setName(split1[1].trim());
                tran.setForeignInvestors(DownloadUtils.parseStrToInteger(split1[4].trim().replace(",", "")) / 1000);
                tran.setForeignCorp(DownloadUtils.parseStrToInteger(split1[7].trim().replace(",", "")) / 1000);
                tran.setInvestmentTrust(DownloadUtils.parseStrToInteger(split1[13].trim().replace(",", "")) / 1000);
                tran.setDealerSelf(DownloadUtils.parseStrToInteger(split1[16].trim().replace(",", "")) / 1000);
                tran.setDealerHedge(DownloadUtils.parseStrToInteger(split1[19].trim().replace(",", "")) / 1000);
                tran.setDealer(DownloadUtils.parseStrToInteger(split1[22].trim().replace(",", "")) / 1000);
                tran.setTotal(DownloadUtils.parseStrToInteger(split1[23].trim().replace(",", "")) / 1000);
                int li = filePath.lastIndexOf("/");
                tran.setDate(DownloadUtils.parseStrToInteger(filePath.substring(li + 1 + 13, li + 1 + 21)));
                tran.setCdUnion(tran.getCode() + "-" + tran.getDate());
                tranList.add(tran);
            }
        } finally {
            sc.close();
            DownloadUtils.deleteFile(filePath);
        }
        return tranList;
    }

    /**
     * 儲存每日股票交易
     *
     * @param tranList
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveStockDaily(List<StockDailyTran> tranList) throws IllegalArgumentException, SaveStockDailyFailedException {
        System.out.println("儲存stockdaily開始");
        long l1 = System.currentTimeMillis();
        List<StockDailyTran> stockDailyTrans = stockDailyRepo.saveAll(tranList);
        long l2 = System.currentTimeMillis();
        System.out.println("儲存stockdaily結束,共花:" + (l2 - l1) + "豪秒");
        if (stockDailyTrans.size() == 0 || stockDailyTrans == null) {
            throw new SaveStockDailyFailedException();
        }
    }

    /**
     * 儲存每日法人交易
     *
     * @param tranList
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCorpDaily(List<CorpDailyTran> tranList) throws IllegalArgumentException, SaveCorpDailyFailedException {
        System.out.println("儲存corpdaily開始");
        long l1 = System.currentTimeMillis();
        List<CorpDailyTran> corpDailyTrans = corpDailyRepo.saveAll(tranList);
        long l2 = System.currentTimeMillis();
        System.out.println("儲存corpdaily結束,共花:" + (l2 - l1) + "豪秒");
        if (corpDailyTrans.size() == 0 || corpDailyTrans == null) {
            throw new SaveCorpDailyFailedException();
        }
    }


    /**
     * 下載集保所股權分佈
     *
     * @return
     */
    private String downloadDistribution() {
        String targetDir = resource.getString("TempDir");
        String fileName = "Distribution.csv";
        String url = resource.getString("DistributionUrl");

        //定義請求頭的接收類型
        RequestCallback requestCallback = request -> {
            request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        };
        // getForObject會將所有返回直接放到內存中,使用流來替代這個操作
        ResponseExtractor<Void> responseExtractor = response -> {
            // Here I write the response to a file but do what you like
            Path path = Paths.get(targetDir);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            return null;
        };
        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        return targetDir + "/" + fileName;
    }

    /**
     * 轉換為pojo
     *
     * @param filePath
     * @return
     */
    private List<Distribution> filtDistribution(String filePath) {
        List<Distribution> tranList = new LinkedList<>();
        String content = DownloadUtils.readFileToString(filePath, "utf-8");
        if (content.length() == 0) {
            return tranList;
        }
        String[] split = content.split("占集保庫存數比例%");
        Scanner sc = new Scanner(split[1]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        Distribution tran = new Distribution();
        try {
            int rate = 0;
            while (sc.hasNext()) {
                content = sc.next();
                if (content.length() == 0) {
                    continue;
                }
                String[] split1 = content.split(",");
                if (split1[1].trim().length() > 4) {
                    continue;
                }
                rate = DownloadUtils.parseStrToInteger(split1[2]);
                switch (rate) {
                    case 11:
                        tran.setCode(split1[1]);
                        tran.setDate(DownloadUtils.parseStrToInteger(split1[0]));
                        tran.setCdUnion(tran.getCode() + "-" + tran.getDate());
                        tran.setRate11(split1[3] + "/" + DownloadUtils.parseStrToInteger(split1[4]) / 1000 + "/" + split1[5]);
                    case 12:
                        tran.setRate12(split1[3] + "/" + DownloadUtils.parseStrToInteger(split1[4]) / 1000 + "/" + split1[5]);
                        break;
                    case 13:
                        tran.setRate13(split1[3] + "/" + DownloadUtils.parseStrToInteger(split1[4]) / 1000 + "/" + split1[5]);
                        break;
                    case 14:
                        tran.setRate14(split1[3] + "/" + DownloadUtils.parseStrToInteger(split1[4]) / 1000 + "/" + split1[5]);
                        break;
                    case 15:
                        tran.setRate15(split1[3] + "/" + DownloadUtils.parseStrToInteger(split1[4]) / 1000 + "/" + split1[5]);
                        break;
                    case 17:
                        tran.setTotal(split1[3] + "/" + DownloadUtils.parseStrToInteger(split1[4]) / 1000 + "/" + split1[5]);
                        tranList.add(tran);
                        tran = new Distribution();
                        break;
                    default:
                        continue;
                }
            }
        } finally {
            sc.close();
            DownloadUtils.deleteFile(filePath);
        }
        return tranList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDistribution(List<Distribution> distributionList) {
        System.out.println("儲存distribution開始");
        long l1 = System.currentTimeMillis();
        distributionList = distributionRepo.saveAll(distributionList);
        long l2 = System.currentTimeMillis();
        System.out.println("儲存corpdaily結束,共花:" + (l2 - l1) + "豪秒");
        if (distributionList.size() == 0 || distributionList == null) {
            throw new SaveCorpDailyFailedException();
        }
    }


}
