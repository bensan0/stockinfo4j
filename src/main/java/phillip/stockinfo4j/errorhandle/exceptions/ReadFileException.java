package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@NoArgsConstructor
public class ReadFileException extends CustomRuntimeException {

    public ReadFileException(ErrorEnum errorEnum, String msg) {
        this.setErrorEnum(errorEnum);
        this.setOriginalMsg(msg);
    }

    ;

}
