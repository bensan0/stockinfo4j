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
    private String rate11;
    private String rate12;
    private String rate13;
    private String rate14;
    private String rate15;
    private String total ;
    private Integer date;
}
