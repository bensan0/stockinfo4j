package phillip.stockinfo4j.errorhandle.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SaveFailException extends RuntimeException{

    public SaveFailException(String msg){
        super(msg);
    }
}
