package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlowlyIncreaseDTO {

    private String code;
    private String name;
    private String industry;
    private Double pastPrice;
    private Double nowPrice;
    private String flucPercent;
    private Long pastTradingVolAvg;
    private Long nowTradingVol;

//    public SlowlyIncreaseDTO(StockIndustryDTO dto) {
//        this.code = dto.getCode();
//        this.name = dto.getName();
//        this.industry = dto.getIndustry();
//    }
}
