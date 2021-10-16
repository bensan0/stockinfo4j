package phillip.stockinfo4j.errorhandle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.CustomRuntimeException;
import phillip.stockinfo4j.errorhandle.exceptions.DeleteFileException;
import phillip.stockinfo4j.errorhandle.exceptions.ReadFileException;
import phillip.stockinfo4j.model.dto.DownloaderRes;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @Autowired
    DownloaderRes resp;

//    @ExceptionHandler(Exception.class)
//    public DownloaderRes handleException(Exception e) {
//        resp.setErrorMessage(ErrorEnum.UnDefinedException);
//        resp.setErrorDetail(e.getMessage());
//        return resp;
//    }

//    @ExceptionHandler(RuntimeException.class)
//    public DownloaderRes handleRuntimeException(RuntimeException e){
//        resp.setErrorMessage(ErrorEnum.UDefinedRuntimeException);
//        resp.setErrorDetail(e.getMessage());
//        return resp;
//    }

    @ExceptionHandler({ReadFileException.class, DeleteFileException.class})
    public DownloaderRes handleCustomRuntimeException(CustomRuntimeException e) {
        resp.setErrorMessage(e.getErrorEnum());
        resp.setErrorDetail(e.getOriginalMsg());
        return resp;
    }

}
