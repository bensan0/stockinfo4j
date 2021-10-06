package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.DownloaderResponse;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;
import phillip.stockinfo4j.model.pojo.StockDailyTran;
import phillip.stockinfo4j.service.IDownloadService;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class DownloadServiceImpl /*implements IDownloadService*/ {

    @Autowired
    private ResourceBundle resource;

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private StockDailyRepo stockDailyRepo;
//
//    @Autowired
//    private CorpDailyRepo corpDailyRepo;


    /**
     * 給外部調用
     *
     * @param date
     * @return
     */
//    @Override
    public DownloaderResponse getDaily(String date) {
        getTPEXCorpDaily(date);
        getTPEXStockDaily(date);
        getTWSECorpDaily(date);
        getTWSEStockDaily(date);
        return null;
    }

    /**
     *
     * @param date
     */
    @Async("executor")
    public void getTWSEStockDaily(String date){
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ":getTWSEStockDaily start");
        try {
            String filePath = downloadTWSEStockDaily(date);
            filtTWSEStockDaily(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(threadName + ":getTWSEStockDaily end");
    }

    /**
     *
     * @param date
     */
    @Async("executor")
    public void getTWSECorpDaily(String date){
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ":getTWSECorpDaily start");
        try {
            String filePath = downloadTWSECorpDaily(date);
            filtTWSECorpDaily(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(threadName + ":getTWSECorpDaily end");
    }

    /**
     *
     * @param date
     */
    @Async("executor")
    public void getTPEXStockDaily(String date){
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ":getTPEXStockDaily start");
        try {
            String filePath = downloadTPEXStockDaily(date);
            filtTPEXStockDaily(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(threadName + ":getTPEXStockDaily end");
    }

    /**
     *
     * @param date
     */
    @Async("executor")
    public void getTPEXCorpDaily(String date){
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ":getTPEXCorpDaily start");
        try {
            String filePath = downloadTPEXCorpDaily(date);
            filtTPEXCorpDaily(filePath);
            Thread.sleep(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e1){
            e1.printStackTrace();
        }
        System.out.println(threadName + ":getTPEXCorpDaily end");
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
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName));
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
    private List<StockDailyTran> filtTWSEStockDaily(String filePath) throws IOException {
        List<StockDailyTran> tranList = new ArrayList<>();
        String content = DownloadUtils.readFileToString(filePath, "Big5");
        if (content.length() == 0) {
            return tranList;
        }
        String[] split = content.split("\"本益比\",");
        Scanner sc = new Scanner(split[1]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        DecimalFormat df = getDecimalFormat();
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
                tran.setTradingVol(parseStrToDouble(split1[2].replace(",", "").trim()).intValue() / 1000);
                tran.setDeal(parseStrToDouble(split1[3].replace(",", "").trim()).intValue());
                tran.setOpening(parseStrToDouble(split1[5].replace(",", "").trim()));
                tran.setHighest(parseStrToDouble(split1[6].replace(",", "").trim()));
                tran.setLowest(parseStrToDouble(split1[7].replace(",", "").trim()));
                tran.setClosing(parseStrToDouble(split1[8].replace(",", "").trim()));
                if (split1[9].equals("+") || split1[9].equals("-")) {
                    tran.setFluc(parseStrToDouble(split1[9].trim() + split1[10].replace(",", "").trim()));
                    Double yesterdayClosing = tran.getClosing() - tran.getFluc();
                    Double flucPer = Double.parseDouble(df.format((tran.getFluc() * 100 / yesterdayClosing)));
                    tran.setFlucPer(flucPer);
                } else {
                    tran.setFluc(0.00);
                    tran.setFlucPer(0.00);
                }
                int li = filePath.lastIndexOf("/");
                tran.setDate(Integer.parseInt(filePath.substring(li + 1 + 14, li + 1 + 22)));
                tran.setCdUnion(tran.getCode() + tran.getDate());
                tran.setPer(parseStrToDouble(split1[15]));
                tranList.add(tran);
            }
        } finally {
            sc.close();
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
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName));
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
    private List<CorpDailyTran> filtTWSECorpDaily(String filePath) throws IOException {
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
                tran.setForeignInvestors(parseStrToInteger(split1[4].trim().replace(",", "")) / 1000);
                tran.setForeignCorp(parseStrToInteger(split1[7].trim().replace(",", "")) / 1000);
                tran.setInvestmentTrust(parseStrToInteger(split1[10].trim().replace(",", "")) / 1000);
                tran.setDealer(parseStrToInteger(split1[11].trim().replace(",", "")) / 1000);
                tran.setDealerSelf(parseStrToInteger(split1[14].trim().replace(",", "")) / 1000);
                tran.setDealerHedge(parseStrToInteger(split1[17].trim().replace(",", "")) / 1000);
                tran.setTotal(parseStrToInteger(split1[18].trim().replace(",", "")) / 1000);
                int li = filePath.lastIndexOf("/");
                tran.setDate(Integer.parseInt(filePath.substring(li + 1 + 13, li + 1 + 21)));
                tran.setCdUnion(tran.getCode() + tran.getDate());
                tranList.add(tran);
            }
        } finally {
            sc.close();
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
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName));
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
    private List<StockDailyTran> filtTPEXStockDaily(String filePath) throws IOException {
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
        DecimalFormat df = getDecimalFormat();
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
                tran.setClosing(parseStrToDouble(split1[2].trim().replace(",", "")));
                tran.setFluc(parseStrToDouble(split1[3].trim().replace(",", "")));
                tran.setOpening(parseStrToDouble(split1[4].trim().replace(",", "")));
                tran.setHighest(parseStrToDouble(split1[5].trim().replace(",", "")));
                tran.setLowest(parseStrToDouble(split1[6].trim().replace(",", "")));
                tran.setTradingVol(parseStrToInteger(split1[8].trim().replace(",", "")) / 1000);
                tran.setDeal(parseStrToInteger(split1[10].trim().replace(",", "")));
                Double yesterdayClosing = tran.getClosing();
                tran.setFlucPer(parseStrToDouble(df.format(tran.getFluc() * 100 / yesterdayClosing)));
                int li = filePath.lastIndexOf("/");
                tran.setDate(parseStrToInteger(filePath.substring(li + 1 + 14, li + 1 + 22)));
                tran.setCdUnion(tran.getCode() + tran.getDate());
                tranList.add(tran);
            }
        } finally {
            sc.close();
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
            Files.copy(response.getBody(), Paths.get(targetDir + "/" + fileName));
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
    private List<CorpDailyTran> filtTPEXCorpDaily(String filePath) throws IOException {
        List<CorpDailyTran> tranList = new LinkedList<>();
        String content = DownloadUtils.readFileToString(filePath, "big5");
        if (content.length() == 0) {
            return tranList;
        }
        String[] split = content.split("三大法人買賣超股數合計");
        Scanner sc = new Scanner(split[1]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        DecimalFormat df = getDecimalFormat();
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
                tran.setForeignInvestors(parseStrToInteger(split1[4].trim().replace(",", "")) / 1000);
                tran.setForeignCorp(parseStrToInteger(split1[7].trim().replace(",", "")) / 1000);
                tran.setInvestmentTrust(parseStrToInteger(split1[13].trim().replace(",", "")) / 1000);
                tran.setDealerSelf(parseStrToInteger(split1[16].trim().replace(",", "")) / 1000);
                tran.setDealerHedge(parseStrToInteger(split1[19].trim().replace(",", "")) / 1000);
                tran.setDealer(parseStrToInteger(split1[22].trim().replace(",", "")) / 1000);
                tran.setTotal(parseStrToInteger(split1[23].trim().replace(",", "")) / 1000);
                int li = filePath.lastIndexOf("/");
                tran.setDate(parseStrToInteger(filePath.substring(li + 1 + 13, li + 1 + 21)));
                tran.setCdUnion(tran.getCode() + tran.getDate());
                tranList.add(tran);
            }
        } finally {
            sc.close();
        }
        return tranList;
    }

    /**
     * 儲存每日股票交易
     * @param tranList
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
//    private void saveStockDaily(List<StockDailyTran> tranList) throws IllegalArgumentException, NullPointerException{
//        List<StockDailyTran> stockDailyTrans = stockDailyRepo.saveAll(tranList);
//        if (stockDailyTrans.size()==0||stockDailyTrans==null){
//            throw new NullPointerException();
//        }
//    }

    /**
     * 儲存每日法人交易
     * @param tranList
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
//    private void saveCorpDaily(List<CorpDailyTran> tranList) throws IllegalArgumentException, NullPointerException{
//        List<CorpDailyTran> corpDailyTrans = corpDailyRepo.saveAll(tranList);
//        if (corpDailyTrans.size()==0||corpDailyTrans==null){
//            throw new NullPointerException();
//        }
//    }

    /**
     * @param str
     * @return
     */
    private Double parseStrToDouble(String str) {
        Double result;
        try {
            result = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.00;
        }
        return result;
    }

    /**
     * @param str
     * @return
     */
    private Integer parseStrToInteger(String str) {
        Integer result;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
        return result;
    }

    /**
     * @return
     */
    private DecimalFormat getDecimalFormat() {
        DecimalFormat df = new DecimalFormat("############.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df;
    }

    public static void main(String[] args) {

    }
}
