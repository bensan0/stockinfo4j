package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.text.DateFormatter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltStockDailyReq implements Serializable {

    private String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    private Double tradingVolFlucPercentLL = 0.40;//今交易量>昨% 下限

    private Double tradingVolFlucPercentUL = 10000.00;//今交易量>昨% 上限

    private Integer yesterdayTradingVolLL = 3000;//昨交易量下限

    private Integer yesterdayTradingVolUL = 100000;//昨交易量上限

    private Double todayClosingUL = 40.00;//今日收盤價上限

    public void setTradingVolFlucPercentLL(Double tradingVolFlucPercentLL) {
        try{
            this.tradingVolFlucPercentLL = tradingVolFlucPercentLL/100;
        }catch (Exception e){
            return;
        }
    }

    public void setTradingVolFlucPercentUL(Double tradingVolFlucPercentUL) {
        try{
            this.tradingVolFlucPercentUL = tradingVolFlucPercentUL/100;
        }catch (Exception e){
            return;
        }
    }

    public void setYesterdayTradingVolLL(Integer yesterdayTradingVolLL) {
        if(yesterdayTradingVolLL==null){
            return;
        }
        this.yesterdayTradingVolLL = yesterdayTradingVolLL;
    }

    public void setYesterdayTradingVolUL(Integer yesterdayTradingVolUL) {
        if(yesterdayTradingVolUL==null){
            return;
        }
        this.yesterdayTradingVolUL = yesterdayTradingVolUL;
    }

    public void setTodayClosingUL(Double todayClosingUL) {
        if(todayClosingUL==null){
            return;
        }
        this.todayClosingUL = todayClosingUL;
    }
}
