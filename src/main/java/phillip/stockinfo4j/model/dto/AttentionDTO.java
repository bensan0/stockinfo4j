package phillip.stockinfo4j.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttentionDTO {

    private String code;
    private String name;
    private String industry;
    private String note;
    @JsonProperty("date")
    private Integer joinDate;
}
