package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@NoArgsConstructor
public class SaveCorpDailyFailedException extends CustomRuntimeException {

    public SaveCorpDailyFailedException(ErrorEnum errorEnum, String msg){
        this.setOriginalMsg(msg);
        this.setErrorEnum(errorEnum);
    }
}
