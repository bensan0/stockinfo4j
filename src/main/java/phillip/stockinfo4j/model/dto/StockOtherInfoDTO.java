package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockOtherInfoDTO implements Serializable {

    private String mainBusiness;
    private String per;
    private String financingBalance;
    private String financingBalanceStatus;
    private String marginBalance;
    private String marginBalanceStatus;
    private String marginFinancing;
    private String marginFinancingStatus;
    private String ma5;
    private String ma5Bias;
    private String ma10;
    private String ma10Bias;
    private String ma20;
    private String ma20Bias;
    private String revenueDate;
    private String revenueMonthFluc;
    private String revenueYearFluc;
}
