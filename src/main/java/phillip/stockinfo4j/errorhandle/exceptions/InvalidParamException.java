package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@Data
@NoArgsConstructor
public class InvalidParamException extends CustomRuntimeException{

    public InvalidParamException(ErrorEnum errorEnum, String originalErrorMsg){
        super(originalErrorMsg,errorEnum);
    }
}
