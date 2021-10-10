package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@NoArgsConstructor
public class SaveStockDailyFailedException extends CustomRuntimeException {

    public SaveStockDailyFailedException(ErrorEnum errorEnum, String msg){
        this.setMsg(msg);
        this.setErrorEnum(errorEnum);
    }
}
