package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.DownloadReturn;
import phillip.stockinfo4j.model.stockdaily.StockDailyTran;
import phillip.stockinfo4j.service.IDownloadService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class DownloadServiceImpl implements IDownloadService {

    @Autowired
    private ResourceBundle resource;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public DownloadReturn getTWSE(String date) {
        try {
//            String filePath = downloadTWSE(date);
            filtTWSE("temp/TWSEStockDaily20210930.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param date
     * @return
     */
    private String downloadTWSE(String date) {
        String targetDir = resource.getString("temp");
        String fileName = "TWSEStockDaily" + date + ".csv";
        String url = resource.getString("TWSEUrl");
        url += date;

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
     * @param filePath
     * @throws IOException
     */
    private List<StockDailyTran> filtTWSE(String filePath) throws IOException {
        List<StockDailyTran> tranList = new ArrayList<>();
        String content = DownloadUtils.readFileToString(filePath, "Big5");
        String[] split = content.split("\"本益比\",");
        Scanner sc = new Scanner(split[1]);
        sc.useDelimiter("\n");
        sc.useDelimiter("\r\n");
        DecimalFormat df = new DecimalFormat("###.00");
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
                if(split1[0].trim().length()>4){
                    continue;
                }
                tran.setCode(split1[0].trim());
                tran.setName(split1[1].trim());
                tran.setTradingVol(parseNum(split1[2].replace(",", "").trim()).intValue());
                tran.setDeal(parseNum(split1[3].replace(",", "").trim()).intValue());
                tran.setOpening(parseNum(split1[5].replace(",","").trim()));
                tran.setHighest(parseNum(split1[6].replace(",","").trim()));
                tran.setLowest(parseNum(split1[7].replace(",","").trim()));
                tran.setClosing(parseNum(split1[8].replace(",","").trim()));
                if(split1[9].equals("+")||split1[9].equals("-")){
                    tran.setFluc(parseNum(split1[9].trim() + split1[10].replace(",", "").trim()));
                    Double yesterdayClosing = tran.getClosing() + tran.getFluc();
                    Double flucPer = Double.parseDouble(df.format((tran.getFluc()/yesterdayClosing)*100));
                    tran.setFlucPer(flucPer);
                }else{
                    tran.setFluc(0.00);
                    tran.setFlucPer(0.00);
                }
                int li = filePath.lastIndexOf("/");
                tran.setDate(Integer.parseInt(filePath.substring(li+1+14,li+1+22)));
                tran.setCdUnion(tran.getCode() + tran.getDate());
                tran.setPer(parseNum(split1[15]));
                tranList.add(tran);
            }
        } finally {
            sc.close();
        }
        return tranList;
    }



    private Double parseNum(String str){
        Double result;
        try{
            result = Double.parseDouble(str);
        }catch (NumberFormatException e){
            return 0.00;
        }
        return result;
    }

    public static void main(String[] args) {
        String a = "TWSEStockDaily20210930.csv";
        int li = a.lastIndexOf("/");
        System.out.println(a.substring(li+1+14, li+1+22));
    }
}
