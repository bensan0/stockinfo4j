package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@Data
@NoArgsConstructor
public class DeleteFileException extends CustomRuntimeException {

    public DeleteFileException(ErrorEnum errorEnum,String originalErrorMsg) {
        super(originalErrorMsg, errorEnum);
    }

}
