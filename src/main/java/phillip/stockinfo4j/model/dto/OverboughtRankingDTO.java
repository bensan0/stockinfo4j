package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverboughtRankingDTO {
    private String code;
    private String name;
    private String industry;
    private Long overbought ;
    private Double closing;
    private Double flucPer;
    private Double tradingAmount;//買賣超金額

    public void setTradingAmount(Double tradingAmount) {
        DecimalFormat df = new DecimalFormat("###########0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        this.tradingAmount = Double.parseDouble(df.format(tradingAmount));
    }
}
