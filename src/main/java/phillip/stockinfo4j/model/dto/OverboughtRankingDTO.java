package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
