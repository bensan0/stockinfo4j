package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public void setDate(String date) {
        if (date == null || date.trim().length() != 8) {
        } else {
            try {
                Integer.parseInt(date);
                this.date = date;
            } catch (NumberFormatException e) {
            }
        }

    }

    public void setTradingVolFlucPercentLL(Double tradingVolFlucPercentLL) {
        this.tradingVolFlucPercentLL = tradingVolFlucPercentLL / 100;
    }

    public void setTradingVolFlucPercentUL(Double tradingVolFlucPercentUL) {
        this.tradingVolFlucPercentUL = tradingVolFlucPercentUL / 100;
    }
}
