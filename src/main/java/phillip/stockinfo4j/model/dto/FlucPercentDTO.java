package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlucPercentDTO {

    private String code;
    private String name;
    private String industry;
    private Double closing;
    private Double fluc;
    private Double flucPer;
}
