package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@Data
@NoArgsConstructor
public class DateParseException extends CustomRuntimeException {
    public DateParseException(ErrorEnum errorEnum, String originalErrorMsg) {
        super(originalErrorMsg, errorEnum);
    }
}
