package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends Exception{
    private String originalErrorMsg;
    private ErrorEnum errorEnum;
}
