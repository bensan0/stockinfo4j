package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomRuntimeException extends RuntimeException {
    private String originalErrorMsg;
    private ErrorEnum errorEnum;
}
