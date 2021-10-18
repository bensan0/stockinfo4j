package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

@Data
@NoArgsConstructor
public class SaveDistributionException extends CustomRuntimeException{

    public SaveDistributionException(ErrorEnum errorEnum,String originalErrorMsg) {
        super(originalErrorMsg, errorEnum);
    }
}
