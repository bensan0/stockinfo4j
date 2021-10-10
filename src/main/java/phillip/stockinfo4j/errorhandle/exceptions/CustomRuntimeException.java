package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.*;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomRuntimeException extends RuntimeException{
    private String originalMsg;
    private ErrorEnum errorEnum;
}
