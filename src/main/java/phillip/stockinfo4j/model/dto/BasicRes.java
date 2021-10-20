package phillip.stockinfo4j.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BasicRes implements Serializable {

    @JsonProperty("errorMsg")
    private ErrorEnum errorEnum = ErrorEnum.Success;
    private String errorDetail = "";
    private Object data;
}
