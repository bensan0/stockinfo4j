package phillip.stockinfo4j.errorhandle.exceptions;

public class SavedFailException extends RuntimeException{

    public SavedFailException(String msg){
        super(msg);
    }
}
