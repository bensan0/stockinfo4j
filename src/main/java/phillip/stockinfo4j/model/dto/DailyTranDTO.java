package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTranDTO {

    private String code;

    private String name;

    private String industry;

    private Long tradingVol;//成交張數

    private Long deal;//成交筆數

    private Long tradingAmount;//成交金額

    private Double opening;//開盤價

    private Double closing;//收盤價

    private Double highest;//盤中最高

    private Double lowest;//盤中最低

    private Double fluc;//漲跌價差

    private Double flucPer;//漲跌幅

    private Integer date;

    private String cdUnion;

    private Double per;//本益比

    private Long foreignInvestors;//外陸資

    private Long foreignCorp;//外資自營商

    private Long investmentTrust;//投信

    private Long dealer;//自營商

    private Long dealerSelf;//自營商自行

    private Long dealerHedge;//自營商避險

    private Long corpTotal;//三大法人合計(不含自營商)
}
