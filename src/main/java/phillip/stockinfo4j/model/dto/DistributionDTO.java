package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionDTO {

    private String code;
    private String name;
    private String industry;
    private String rate11;//200-400
    private String rate12;//400-600
    private String rate13;//600-800
    private String rate14;//800-1000
    private String rate15;//1000 up
    private String total ;
    private Integer date;
}
