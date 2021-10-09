package phillip.stockinfo4j.errorhandle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import phillip.stockinfo4j.errorhandle.exceptions.SaveFailException;
import phillip.stockinfo4j.model.dto.DownloaderResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @Autowired
    DownloaderResponse resp;

    @ExceptionHandler({IOException.class,ExecutionException.class,InterruptedException.class})
    public DownloaderResponse handleIOException(Exception e){
        resp.getErrorDetail().add(e.getMessage());
        return resp;
    }

    @ExceptionHandler(SaveFailException.class)
    public DownloaderResponse handleSaveFailException(SaveFailException e){
        resp.getErrorDetail().add(e.getMessage());
        return resp;
    }

}
