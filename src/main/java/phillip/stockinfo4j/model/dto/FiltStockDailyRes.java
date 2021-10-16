package phillip.stockinfo4j.model.dto;

import lombok.Data;
import phillip.stockinfo4j.model.pojo.StockDailyTran;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FiltStockDailyRes implements Serializable {

    private String code;
    private String name;
    private String industry;
    private List<StockDailyTran> tranList = new ArrayList<>();

    public void setFiltStockDailyDTO(FiltStockDailyDTO dto){
        this.code = dto.getCode();
        this.name = dto.getName();
        this.industry = dto.getIndustry();
    }

}
