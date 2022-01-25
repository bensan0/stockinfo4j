package phillip.stockinfo4j.errorhandle;

import org.springframework.web.bind.annotation.ExceptionHandler;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.*;
import phillip.stockinfo4j.model.dto.BasicRes;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {


    @ExceptionHandler(Exception.class)
    public BasicRes handleException(Exception e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.UnDefinedException);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(RuntimeException.class)
    public BasicRes handleRuntimeException(RuntimeException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.UDefinedRuntimeException);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler({ReadFileException.class, DeleteFileException.class,
            DateParseException.class, InvalidParamException.class,
            BackupFailedException.class, SaveCorpDailyFailedException.class,
            SaveStockDailyFailedException.class, SaveDistributionException.class})
    public BasicRes handleCustomException1(CustomException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(e.getErrorEnum());
        resp.setErrorDetail(e.getOriginalErrorMsg());
        return resp;
    }

    @ExceptionHandler({SQLException.class})
    public BasicRes handleSQLException(SQLException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.FailedSave);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(ExecutionException.class)
    public BasicRes handleExecutionException(ExecutionException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.ExecutionError);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(InterruptedException.class)
    public BasicRes handleInterruptedException(InterruptedException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.ThreadInterrupted);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

}
