package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@NoArgsConstructor
public class DeleteFileException extends CustomRuntimeException {

    public DeleteFileException(ErrorEnum errorEnum, String msg) {
        this.setErrorEnum(errorEnum);
        this.setOriginalMsg(msg);
    }
}
