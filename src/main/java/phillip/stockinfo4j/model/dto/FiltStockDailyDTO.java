package phillip.stockinfo4j.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.model.pojo.StockDailyTran;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class FiltStockDailyDTO implements Serializable {

    private String code;
    private String name;
    private String industry;
    private List<DailyTranDTO> tranList = new ArrayList<>();

}
