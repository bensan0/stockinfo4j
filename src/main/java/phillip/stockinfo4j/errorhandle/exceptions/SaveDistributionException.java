package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@NoArgsConstructor
public class SaveDistributionException extends CustomRuntimeException{

    public SaveDistributionException(ErrorEnum errorEnum, String msg){
        this.setOriginalMsg(msg);
        this.setErrorEnum(errorEnum);
    }
}
