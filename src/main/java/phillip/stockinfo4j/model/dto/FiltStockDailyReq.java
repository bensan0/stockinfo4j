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

    private Double flucPercentLL = 0.40;

    private Double flucPercentUL = 10000.00;

    private Integer yesterdayTradingVolLL = 3000;

    private Integer yesterdayTradingVolUL = 100000;

    private Double todayClosingUL = 40.00;

    public void setDate(String date) {
        if (date == null || date.trim().length() != 8) {
        } else {
            try {
                int dateInt = Integer.parseInt(date);
                this.date = date;
            } catch (NumberFormatException e) {
            }
        }

    }

    public void setFlucPercentLL(Double flucPercentLL) {
        this.flucPercentLL = flucPercentLL / 100;
    }

    public void setFlucPercentUL(Double flucPercentUL) {
        this.flucPercentUL = flucPercentUL / 100;
    }
}
